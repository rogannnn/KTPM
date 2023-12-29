package com.luv2code.doan.service.impl;


import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.CartMoreThanProductInStock;
import com.luv2code.doan.exceptions.OrderNotFoundException;
import com.luv2code.doan.repository.OrderRepository;
import com.luv2code.doan.repository.OrderStatusRepository;
import com.luv2code.doan.repository.ProductRepository;
import com.luv2code.doan.service.OrderService;
import com.luv2code.doan.service.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    public static final int ORDERS_PER_PAGE = 5;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionService promotionService;


    public Order createOrder(Double totalPrice, Address address, User user, List<Cart> cartList) throws CartMoreThanProductInStock {
        Order newOrder = new Order();
        newOrder.setTotalPrice(totalPrice);
        newOrder.setAddress(address);
        newOrder.setDate(new Date());
        newOrder.setUser(user);

        List<OrderDetail> orderDetailSet = newOrder.getOrderDetails();

        for (Cart cart : cartList) {
            Product product = cart.getProducts();

            product.setSoldQuantity(product.getSoldQuantity() + cart.getQuantity());

            if(product.getInStock() - cart.getQuantity() < 0) {
                throw new CartMoreThanProductInStock("The requested quantity exceeds the remaining quantity of this product!");
            }

            product.setInStock(product.getInStock() - cart.getQuantity());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cart.getQuantity());
            Integer promotionPercentage = promotionService.getCurrentPromotionByProduct(product);
            Double price = product.getPrice();
            if(  promotionPercentage > 0) { //price with discount
                Double discountAmount = price * (promotionPercentage / 100.0);
                Double priceAfterDiscount = price - discountAmount;
                orderDetail.setUnitPrice(priceAfterDiscount);
            }
            else orderDetail.setUnitPrice(price);

            orderDetailSet.add(orderDetail);
        }

        newOrder.setStatus(orderStatusRepository.getOrderStatusById(1));

        return orderRepository.save(newOrder);
    }


    public void changeOrderStatus(Order order, Integer statusId) {
        if(statusId == 4) { //done
            // will subtract quantity and increase sold quantity
            List<OrderDetail> orderDetail = orderRepository.getOrderDetail(order.getId());

            for(OrderDetail item : orderDetail)
            {
                Product product = item.getProduct();
                product.setSoldQuantity(product.getSoldQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
        else if(statusId == 5) { //cancel
            // will increase quantity

            List<OrderDetail> orderDetail = orderRepository.getOrderDetail(order.getId());

            for(OrderDetail item : orderDetail)
            {
                Product product = item.getProduct();
                product.setInStock(product.getSoldQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
        order.setStatus(orderStatusRepository.getOrderStatusById(statusId));
        orderRepository.save(order);
    }

    public Page<Order> getListOrdersAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword, Integer statusId) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        if(statusId != null) {

            if (keyword != null) {
                return orderRepository.getListOrdersAdminWithKeywordAndStatusId(keyword, statusId, pageable);
            } else {
                return orderRepository.getListOrdersAdminWithStatusId(statusId, pageable);
            }
        }
        else {
            if (keyword != null) {
                return orderRepository.getListOrdersAdminWithKeyword(keyword, pageable);
            } else {
                return orderRepository.getListOrdersAdmin(pageable);
            }
        }

    }

    public Page<Order> listOrderByUser(User user, Integer pageNum, Integer pageSize, Date fromDate, Date toDate) {
        Sort sort = Sort.by("date");
        sort = sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        if(fromDate != null && toDate !=  null) {
            return orderRepository.findOrderByUserBetweenDate(user.getId(), fromDate, toDate, pageable);
        }
        return orderRepository.findOrderByUser(user.getId(), pageable);
    }



    public Order getOrder(Integer id, User user) throws OrderNotFoundException {
        try {
            return orderRepository.findByIdAndUser(id, user.getId());


        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }

    public Order getOrderById(Integer id) throws OrderNotFoundException {
        try {
            return orderRepository.findByOrderId(id);
        }
        catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);

        }
    }


    public boolean isUserHasBuyProduct(Integer userId, Integer productId) {
        long num = orderRepository.countOrderByProductAndUser(userId, productId);
        System.out.println("userId: " + userId + ", productId: " + productId + ", num: " + num);
        return num > 0;
    }


    public Page<Order> getOrderByUserAndStatus(Integer userId, Integer statusId, Integer pageNum, Integer pageSize, Date fromDate, Date toDate) {
        Sort sort = Sort.by("date");
        sort = sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        if(fromDate != null && toDate !=  null) {
            return orderRepository.getOrderByUserAndStatusBetweenDate(userId, statusId, fromDate, toDate, pageable);
        }
        return orderRepository.getOrderByUserAndStatus(userId, statusId, pageable);
    }
}
