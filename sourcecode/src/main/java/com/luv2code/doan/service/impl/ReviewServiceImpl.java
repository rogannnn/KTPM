package com.luv2code.doan.service.impl;


import com.luv2code.doan.entity.Review;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.ReviewNotFoundException;
import com.luv2code.doan.repository.ReviewRepository;
import com.luv2code.doan.service.ProductService;
import com.luv2code.doan.service.ReviewService;
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


@Service
public class ReviewServiceImpl implements ReviewService {
    private final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);
    public static final int REVIEW_PER_PAGE = 9;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }


    public Page<Review> getReviewByProduct(Integer productId, int pageNo, int pageSize, String sortField, String sortDirection) throws ProductNotFoundException {

        productService.getProductById(productId);

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);


        return reviewRepository.findReviewByProduct(productId, pageable);
    }


    public List<Review> getAllReviewByProduct(Integer productId) throws ProductNotFoundException {
        productService.getProductById(productId);
        return reviewRepository.findAllReviewByProduct(productId);

    }
    public Page<Review> getReviewByUser(Integer userId, int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);

        return reviewRepository.findReviewByUser(userId, pageable);
    }


    public Integer getCountByProductId(Integer productId) {
        return reviewRepository.countByProductId(productId);
    }

    public Integer getCountStarNumByProduct(Integer id, Integer starNum) {
        return  reviewRepository.countStarNumByProduct(id, starNum);
    }

    public Review getReviewByUserIdAndProductId(Integer userId, Integer productId) {
        return  reviewRepository.findReviewByUserAndProduct(userId, productId);
    }

    public void deleteReview(Integer id) throws ReviewNotFoundException {
        Long count = reviewRepository.countById(id);
        if (count == null || count == 0) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }

        reviewRepository.deleteById(id);
    }

    public Review getReviewById(Integer id) throws ReviewNotFoundException {
        try {
            return reviewRepository.findById(id).get();

        }
        catch(NoSuchElementException ex) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);

        }
    }

    public Review getReviewByIdAndUserId(Integer id, Integer userId) throws ReviewNotFoundException {
        Review review =  reviewRepository.findReviewByIdAndUserId(id, userId);
        if(review != null) {
            return review;
        }
        else {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }

    }

    public Review getReviewByProductIdAndUserId(Integer productId, Integer userId) {
        return reviewRepository.findReviewByProductIdAndUserId(productId, userId);
    }

}
