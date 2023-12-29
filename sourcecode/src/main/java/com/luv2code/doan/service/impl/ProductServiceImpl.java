package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.CategoryRepository;
import com.luv2code.doan.repository.ProductRepository;
import com.luv2code.doan.service.ProductService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.DecimalFormat;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Product getProductByName(String name) {
        Product product = productRepository.getProductByName(name);

        return product;
    }


    public int getCountByCategoryId(Integer categoryId) throws CategoryNotFoundException {
        try {
            categoryRepository.findById(categoryId).get();
            return (int) productRepository.countByCategory(categoryId);
        }
        catch(NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + categoryId);

        }
    }

    public int getCount() {
        return (int) productRepository.count();
    }

    public List<Product> getAllByCategoryId(Integer categoryId, int pageNo, int pageSize, String sortField, String sortDirection) throws CategoryNotFoundException {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);

        try {
            categoryRepository.findById(categoryId).get();
        }
        catch(NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + categoryId);

        }
        return productRepository.findAllByCategory(categoryId, pageable);
    }

    public List<Product> getListProducts( int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        return productRepository.getListProducts(pageable);
    }

    public Page<Product> getListProductsSearch( int pageNo, int pageSize, String sortField, String sortDirection, String keyword, Double minPrice, Double maxPrice, List<Integer> listCategoryIds) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        if(listCategoryIds != null) {
            return productRepository.searchWithKeywordFilterProductWithCategory(keyword, listCategoryIds, pageable);
        }
        else {
            return productRepository.searchWithKeywordFilterProduct(keyword, pageable);
        }
    }

    public Page<Product> getListProductsAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        if(keyword != null) {
            return productRepository.getListProductsAdminWithKeyword(keyword, pageable);
        }
        else {
            return productRepository.getListProductsAdmin(pageable);
        }
    }

    public Product getProductById(Integer id) throws ProductNotFoundException {
        try {
            Product product = productRepository.findById(id).get();
            return product;

        }
        catch(NoSuchElementException ex) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);

        }
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }


    public void deleteProduct(Integer id) throws ProductNotFoundException {
        try {
            Product product = productRepository.findById(id).get();
            product.setIsActive(false);
            productRepository.save(product);

        }
        catch(NoSuchElementException ex) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    public void approveProduct(Integer id) throws ProductNotFoundException {
        try {
            Product product = productRepository.findById(id).get();
            product.setIsActive(true);
            productRepository.save(product);

        }
        catch(NoSuchElementException ex) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    public Page<Product> listLatestProduct() {
        Pageable pageable = PageRequest.of(0, 10);
        return productRepository.findLatestProduct(pageable);
    }

    public Page<Product> listBestSellProduct() {
        Pageable pageable = PageRequest.of(0,10);
        return productRepository.findBestSellProduct(pageable);
    }


    public Double getMaxPrice() {
        return productRepository.getMaxPrice();
    }

    public Page<String> getSuggest(String keyword) {
        Pageable pageable = PageRequest.of(0, 10);
        return productRepository.search(keyword, pageable);
    }
}