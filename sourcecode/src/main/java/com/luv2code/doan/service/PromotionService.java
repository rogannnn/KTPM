package com.luv2code.doan.service;

import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Promotion;
import com.luv2code.doan.exceptions.BrandNotFoundException;
import com.luv2code.doan.exceptions.PromotionNotFoundException;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface PromotionService {

    public Promotion getPromotionById(Integer id) throws PromotionNotFoundException;

    public Promotion savePromotion(Promotion promotion);

    public Page<Promotion> getListPromotionsAdmin( int pageNo, int pageSize, String sortField, String sortDirection);

    public void deletePromotion(Integer id) throws PromotionNotFoundException;

    public void approvePromotion(Integer id) throws PromotionNotFoundException;

    public List<Promotion> findPromotion(Date startDate, Date finishDate);

    public int getCurrentPromotionByProduct(Product product);
}
