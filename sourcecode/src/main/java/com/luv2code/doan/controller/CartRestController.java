package com.luv2code.doan.controller;

import com.luv2code.doan.dto.*;
import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.CartMoreThanProductInStock;
import com.luv2code.doan.exceptions.NotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.request.CartRequest;
import com.luv2code.doan.response.BaseResponse;
import com.luv2code.doan.response.CartReponse;
import com.luv2code.doan.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PromotionService promotionService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getListCart(HttpServletRequest request, Authentication authentication) throws UserNotFoundException, NotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userService.getUserByEmail(userPrincipal.getEmail());

        List<Cart> listCarts = cartService.findCartByUser(user.getId());

        List<CartDto> listCartDto = new ArrayList<>();
        double total = 0;
        for(Cart c : listCarts) {
            Product product = c.getProducts();
            ProductDto productDto = new ProductDto(product, promotionService.getCurrentPromotionByProduct(product));
            listCartDto.add(new CartDto(c, productDto));
            total = c.getQuantity() * productDto.getPriceAfterDiscount();
        }


        ListCartDto result = new ListCartDto(1, "Get list cart successfully!", request.getMethod(), new Date().getTime(),
                HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), listCartDto, total);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartRequest cartRequest, HttpServletRequest request,
                                       Authentication authentication) throws UserNotFoundException, ProductNotFoundException, CartMoreThanProductInStock, NotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userService.getUserByEmail(userPrincipal.getEmail());

        Product product = productService.getProductById(cartRequest.getId());

        Cart cart = cartService.addProductToCart(product, user);

        ProductDto productDto = new ProductDto(product, promotionService.getCurrentPromotionByProduct(product));
        CartDto cartDto = new CartDto(cart, productDto);

        CartReponse cartReponse = new CartReponse(1, "Add product to cart successfully!", request.getMethod(), new Date().getTime(),
                HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), cartDto);
        return new ResponseEntity<>(cartReponse, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateCart(@Valid @RequestBody CartRequest cartRequest, HttpServletRequest request,
                                        Authentication authentication) throws UserNotFoundException, ProductNotFoundException, CartMoreThanProductInStock {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userService.getUserByEmail(userPrincipal.getEmail());

        Product product = productService.getProductById(cartRequest.getId());
        int quantityProduct = cartRequest.getQuantity();

        if(quantityProduct <= 0) {
            BaseResponse baseResponse = new BaseResponse(0, "Quantity of cart item must be greater than 0!", request.getMethod(), new Date().getTime(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        Cart cart = cartService.updateCart(product, user, quantityProduct);

        ProductDto productDto = new ProductDto(product, promotionService.getCurrentPromotionByProduct(product));
        CartDto cartDto = new CartDto(cart, productDto);

        CartReponse cartReponse = new CartReponse(1, "Update cart successfully!", request.getMethod(), new Date().getTime(),
                HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), cartDto);
        return new ResponseEntity<>(cartReponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteCart(@PathVariable Integer id, HttpServletRequest request,
                                        Authentication authentication) throws UserNotFoundException, NotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        userService.getUserByEmail(userPrincipal.getEmail());

        Cart cart = cartService.findCartById(id);
        cartService.deleteCartById(id);

        Product product = cart.getProducts();
        ProductDto productDto = new ProductDto(product, promotionService.getCurrentPromotionByProduct(product));
        CartDto cartDto = new CartDto(cart, productDto);

        CartReponse cartReponse = new CartReponse(1, String.valueOf(id), request.getMethod(), new Date().getTime(),
                HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), cartDto);
        return new ResponseEntity<>(cartReponse, HttpStatus.OK);
    }
}
