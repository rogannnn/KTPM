package com.luv2code.doan.service;

import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.CartMoreThanProductInStock;
import com.luv2code.doan.exceptions.NotFoundException;
import com.luv2code.doan.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;


public interface CartService {

    public Cart addProductToCart(Product product, User user) throws CartMoreThanProductInStock;

    public Cart updateCart(Product product, User user, Integer quantity) throws CartMoreThanProductInStock;

    public Cart findCartByUserAndProduct(Integer userId, Integer productId) throws NotFoundException;

    public List<Cart> findCartByUser(Integer id);

    public Cart findCartById(Integer id) throws NotFoundException;

    public void deleteCartItem(Integer userId, Integer productId);

    public void deleteCartItemByUser(Integer userId, List<Integer> listProductIds);

    public void deleteCartById(Integer id);

}


