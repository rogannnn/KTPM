package com.luv2code.doan.service;

import com.luv2code.doan.entity.ImageProduct;
import com.luv2code.doan.entity.Product;

import java.util.List;


public interface ImageProductService {
    public void addImageProduct(ImageProduct imageProduct);

    public List<String> getListImagesByProduct(Product product);

    public void deleteImageProduct(ImageProduct imageProduct);
}
