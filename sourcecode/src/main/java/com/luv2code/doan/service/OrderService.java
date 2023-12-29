package com.luv2code.doan.service;


import com.luv2code.doan.dto.CartDto;
import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.CartMoreThanProductInStock;
import com.luv2code.doan.exceptions.OrderNotFoundException;
import com.luv2code.doan.repository.OrderRepository;
import com.luv2code.doan.repository.OrderStatusRepository;
import com.luv2code.doan.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


public interface OrderService {


    public Order createOrder(Double totalPrice, Address address, User user, List<Cart> cartList) throws CartMoreThanProductInStock;


    public void changeOrderStatus(Order order, Integer statusId);

    public Page<Order> getListOrdersAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword, Integer statusId);

    public Page<Order> listOrderByUser(User user, Integer pageNum, Integer pageSize, Date fromDate, Date toDate);


    public Order getOrder(Integer id, User user) throws OrderNotFoundException;

    public Order getOrderById(Integer id) throws OrderNotFoundException;


    public boolean isUserHasBuyProduct(Integer userId, Integer productId);


    public Page<Order> getOrderByUserAndStatus(Integer userId, Integer statusId, Integer pageNum, Integer pageSize, Date fromDate, Date toDate);
}
