package com.luv2code.doan.service;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.NotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

public interface ExcelService {
    public List<Product> convertExcelToListOfProduct(InputStream is) throws NotFoundException;
}
