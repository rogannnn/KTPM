package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.OrderStatus;
import com.luv2code.doan.exceptions.OrderStatusNotFoundException;
import com.luv2code.doan.repository.OrderStatusRepository;
import com.luv2code.doan.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    public List<OrderStatus> listOrderStatus() {
        return orderStatusRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public OrderStatus getOrderStatusById(Integer id) throws OrderStatusNotFoundException {
        try {
            return orderStatusRepository.findById(id).get();
        }
        catch(NoSuchElementException ex) {
            throw new OrderStatusNotFoundException("Could not find any order status with ID " + id);

        }
    }
}
