package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.CartMoreThanProductInStock;
import com.luv2code.doan.exceptions.NotFoundException;
import com.luv2code.doan.repository.CartRepository;
import com.luv2code.doan.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart addProductToCart(Product product, User user) throws CartMoreThanProductInStock {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if(cartItem != null) {
//            if(cartItem.getQuantity() > product.getInStock()) {
//                throw new CartMoreThanProductInStock("The requested quantity exceeds the remaining quantity of this product!");
//            }
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        else {
            cartItem = new Cart();
            cartItem.setProducts(product);
            cartItem.setUser(user);
            cartItem.setQuantity(1);

        }
        cartRepository.save(cartItem);
        return cartItem;
    }

    public Cart updateCart(Product product, User user, Integer quantity) throws CartMoreThanProductInStock {
        Cart cartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if(cartItem != null) {
//            if(cartItem.getQuantity() > product.getInStock()) {
//                throw new CartMoreThanProductInStock("The requested quantity exceeds the remaining quantity of this product!");
//            }
        }
        else {
            cartItem = new Cart();
            cartItem.setProducts(product);
            cartItem.setUser(user);
        }

        cartItem.setQuantity(quantity);

        cartRepository.save(cartItem);
        return cartItem;
    }


    public Cart findCartByUserAndProduct(Integer userId, Integer productId) throws NotFoundException {
        return cartRepository.findByUserIdAndProductId(userId, productId);
    }

    public List<Cart> findCartByUser(Integer id) {
        return cartRepository.findByUserId(id);
    }

    public Cart findCartById(Integer id) throws NotFoundException {
        try{
            return cartRepository.findById(id).get();
        }
        catch (NoSuchElementException ex) {
            throw new NotFoundException("Could not find any cart with ID " + id);
        }
    }
    public void deleteCartItem(Integer userId, Integer productId) {
        cartRepository.deleteByUserAndProduct(userId, productId);
    }

    public void deleteCartItemByUser(Integer userId, List<Integer> listProductIds) {
        for(Integer productId : listProductIds) {
            deleteCartItem(userId, productId);
        }
    }

    public void deleteCartById(Integer id)  {
        cartRepository.deleteById(id);

    }


}


