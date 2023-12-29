package com.luv2code.doan.service;

import com.luv2code.doan.entity.OrderStatus;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.OrderStatusNotFoundException;
import com.luv2code.doan.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


public interface OrderStatusService {

    public List<OrderStatus> listOrderStatus();

    public OrderStatus getOrderStatusById(Integer id) throws OrderStatusNotFoundException;
}
