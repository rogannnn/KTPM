package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.ImageProduct;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.repository.ImageProductRepository;
import com.luv2code.doan.service.ImageProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ImageProductServiceImpl implements ImageProductService {

    @Autowired
    private ImageProductRepository imageProductRepository;

    @Override
    public void addImageProduct(ImageProduct imageProduct) {
        imageProductRepository.save(imageProduct);
    }

    @Override
    public List<String> getListImagesByProduct(Product product) {
        return imageProductRepository.getListImagesByProduct(product.getId());
    }

    @Override
    public void deleteImageProduct(ImageProduct imageProduct) {
         imageProductRepository.deleteImageProductById(imageProduct.getId());
    }
}
