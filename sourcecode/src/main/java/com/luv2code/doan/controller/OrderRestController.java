package com.luv2code.doan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.doan.dto.*;
import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.*;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.request.AddressRequest;
import com.luv2code.doan.request.ChangeOrderStatusRequest;
import com.luv2code.doan.request.OrderRequest;
import com.luv2code.doan.response.*;
import com.luv2code.doan.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderRestController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PromotionService promotionService;


    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private ResponseEntity<?> addOrder(@RequestBody String json, Authentication authentication, HttpServletRequest request) throws NotFoundException, UserNotFoundException, AddressNotFoundException, CartMoreThanProductInStock, ProductNotFoundException, JsonProcessingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();


        User user = userService.getUserByEmail(userPrincipal.getEmail());
        OrderRequest orderRequest = new ObjectMapper().readValue(json, OrderRequest.class);


        Address address = addressService.getAddress(orderRequest.getAddressId(),user.getId());

        List<Cart> listCart = new ArrayList<>();
        List<Integer> listProductIds = new ArrayList<>();

        for(CartDto cartDto : orderRequest.getListCart()) {
            Product productInCart = productService.getProductById(cartDto.getProduct().getId());

            listCart.add(new Cart(cartDto.getId(), productInCart, user, cartDto.getQuantity()));
            listProductIds.add(cartDto.getProduct().getId());

        }

        orderService.createOrder(orderRequest.getTotalPrice(), address, user, listCart);


        cartService.deleteCartItemByUser(user.getId(), listProductIds);

        BaseResponse result = new BaseResponse(1, "Save order successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value()
        );

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    private ResponseEntity<?> getOrdersByUserAndStatus(@PathVariable("id") Integer id,
                                                       @RequestParam(value = "fromDate", required = false) Optional<String> pFromDate,
                                                       @RequestParam(value = "toDate", required = false) Optional<String> pToDate,
                                                       @RequestParam(value = "pageNo", required = false) Optional<Integer> pPageNo,
                                                       @RequestParam(value = "pageSize", required = false) Optional<Integer> pPageSize,
                                                       Authentication authentication, HttpServletRequest request) throws UserNotFoundException, OrderStatusNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUserByEmail(userPrincipal.getEmail());

        int pageNo = 1;
        int pageSize = 10;
        Date fromDate = null;
        Date toDate = null;

        if (pPageNo.isPresent()) {
            pageNo = pPageNo.get();
        }
        if (pPageSize.isPresent()) {
            pageSize = pPageSize.get();
        }
        if(pFromDate.isPresent()) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                fromDate = format.parse(pFromDate.get());
                System.out.println(fromDate.toString());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(pToDate.isPresent()) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                toDate = format.parse(pToDate.get());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int totalPage = 0;


        List<Order> listOrders = new ArrayList<>();
        Page page = null;
        if(id == 0) {
            page = orderService.listOrderByUser(user, pageNo, pageSize, fromDate, toDate);
        }
        else {
            OrderStatus orderStatus = orderStatusService.getOrderStatusById(id);
            page = orderService.getOrderByUserAndStatus(user.getId(), orderStatus.getId(), pageNo, pageSize, fromDate, toDate);
        }

        listOrders = page.getContent();

        totalPage = page.getTotalPages();


        List<OrderDto> listOrderDto = new ArrayList<>();
        for(Order order : listOrders) {
            OrderDto orderDto = new OrderDto(order);

            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            List<OrderDetail> listOrderDetail = order.getOrderDetails();

            for(OrderDetail orderDetail : listOrderDetail) {
                Product product = orderDetail.getProduct();
                OrderDetailDto orderDetailDto = new OrderDetailDto(orderDetail,
                        new ProductDto(product, promotionService.getCurrentPromotionByProduct(product)));
                listOrderDetailDto.add(orderDetailDto);
            }
            orderDto.setOrderDetails(listOrderDetailDto);

            listOrderDto.add(orderDto);
        }

        ListOrderResponse result = new ListOrderResponse(1, "Get list order successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                listOrderDto, totalPage, pageNo
        );
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    private ResponseEntity<?> getOrderDetailByUserAndOrder(@PathVariable("id") Integer id, Authentication authentication, HttpServletRequest request) throws UserNotFoundException, OrderStatusNotFoundException, OrderNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUserByEmail(userPrincipal.getEmail());


        Order order = orderService.getOrder(id, user);

        List<OrderDetailDto> orderDetailsDto = new ArrayList<>();

        for(OrderDetail od : order.getOrderDetails()) {
            Review review = reviewService.getReviewByUserIdAndProductId(user.getId(), od.getProduct().getId());
            Product product = od.getProduct();
            if(review != null) {
                orderDetailsDto.add(new OrderDetailDto(od, new ProductDto(product, promotionService.getCurrentPromotionByProduct(product)), true));
            }
            else {
                orderDetailsDto.add(new OrderDetailDto(od, new ProductDto(product, promotionService.getCurrentPromotionByProduct(product)), false));
            }

        }

        OrderDto orderDto = new OrderDto(order);
        List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
        List<OrderDetail> listOrderDetail = order.getOrderDetails();

        for(OrderDetail orderDetail : listOrderDetail) {
            Product product = orderDetail.getProduct();
            OrderDetailDto orderDetailDto = new OrderDetailDto(orderDetail,
                    new ProductDto(product, promotionService.getCurrentPromotionByProduct(product)));
            listOrderDetailDto.add(orderDetailDto);
        }
        orderDto.setOrderDetails(listOrderDetailDto);


        OrderResponse result = new OrderResponse(1, "Get list order detail successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                orderDto
        );
        return new ResponseEntity(result, HttpStatus.OK);
    }


//        '1','Chờ xác nhận'
//        '2','Chờ lấy hàng'
//        '3','Đang giao'
//        '4','Đã giao'
//        '5','Đã hủy'
//        '6','Yêu cầu huỷ'


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private ResponseEntity<?> changeOrderStatus(@PathVariable("id") Integer id, @Valid @RequestBody ChangeOrderStatusRequest changeOrderStatusRequest, Authentication authentication, HttpServletRequest request) throws UserNotFoundException, OrderNotFoundException, OrderStatusNotFoundException, OrderStatusNotValidException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUserByEmail(userPrincipal.getEmail());


        Order order = orderService.getOrder(id, user); //return exception not found order

        OrderStatus orderStatus = orderStatusService.getOrderStatusById(changeOrderStatusRequest.getStatusId()); // return exception not found order not found


        switch (order.getStatus().getId()) {
            case 1:
            case 2: { // Cho xac nhan hoac cho lay hang
                if(orderStatus.getId() == 6) {
                    // Nguoi dung yeu cau huy
                    orderService.changeOrderStatus(order, orderStatus.getId());
                }
                else {
                    // Vuot qua tien do
                    throw new OrderStatusNotValidException("Order status not valid!");
                }
                break;
            }
            default:{
                break;
            }

        }

        OrderDto orderDto = new OrderDto(order);
        List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
        List<OrderDetail> listOrderDetail = order.getOrderDetails();

        for(OrderDetail orderDetail : listOrderDetail) {
            Product product = orderDetail.getProduct();
            OrderDetailDto orderDetailDto = new OrderDetailDto(orderDetail,
                    new ProductDto(product, promotionService.getCurrentPromotionByProduct(product)));
            listOrderDetailDto.add(orderDetailDto);
        }
        orderDto.setOrderDetails(listOrderDetailDto);

        OrderResponse result = new OrderResponse(1, "Change order status order successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                orderDto
        );
        return new ResponseEntity(result, HttpStatus.OK);
    }



}
