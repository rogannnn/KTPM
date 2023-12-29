package com.luv2code.doan.service;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Promotion;
import com.luv2code.doan.entity.PromotionDetail;
import com.luv2code.doan.exceptions.PromotionNotFoundException;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface PromotionDetailService {

    public PromotionDetail savePromotionDetail(PromotionDetail promotion);
    public void deletePromotionDetailByPromotionId(Integer promotionId);
    public PromotionDetail getPromotionDetailById(Integer id);


}
