package com.luv2code.doan.service;


import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Review;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.ReviewNotFoundException;
import com.luv2code.doan.repository.ProductRepository;
import com.luv2code.doan.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


public interface ReviewService {
    public static final int REVIEW_PER_PAGE = 9;


    public Review saveReview(Review review);


    public Page<Review> getReviewByProduct(Integer productId, int pageNo, int pageSize, String sortField, String sortDirection) throws ProductNotFoundException;


    public List<Review> getAllReviewByProduct(Integer productId) throws ProductNotFoundException;

    public Page<Review> getReviewByUser(Integer userId, int pageNo, int pageSize, String sortField, String sortDirection);


    public Integer getCountByProductId(Integer productId);

    public Integer getCountStarNumByProduct(Integer id, Integer starNum);

    public Review getReviewByUserIdAndProductId(Integer userId, Integer productId);

    public void deleteReview(Integer id) throws ReviewNotFoundException;

    public Review getReviewById(Integer id) throws ReviewNotFoundException;

    public Review getReviewByIdAndUserId(Integer id, Integer userId) throws ReviewNotFoundException;

    public Review getReviewByProductIdAndUserId(Integer productId, Integer userId);

}
