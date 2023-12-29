package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Promotion;
import com.luv2code.doan.entity.PromotionDetail;
import com.luv2code.doan.exceptions.PromotionNotFoundException;
import com.luv2code.doan.repository.PromotionDetailRepository;
import com.luv2code.doan.repository.PromotionRepository;
import com.luv2code.doan.service.PromotionDetailService;
import com.luv2code.doan.service.PromotionService;
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
public class PromotionDetailServiceImpl implements PromotionDetailService {
    @Autowired
    private PromotionDetailRepository promotionDetailRepository;

    @Override
    public PromotionDetail getPromotionDetailById(Integer id) {
        PromotionDetail promotion = promotionDetailRepository.findById(id).get();
        return promotion;
    }

    @Override
    public PromotionDetail savePromotionDetail(PromotionDetail promotion) {
        return promotionDetailRepository.save(promotion);
    }

    public void deletePromotionDetailByPromotionId(Integer promotionId) {
        promotionDetailRepository.deleteByPromotionId(promotionId);
    }


}
