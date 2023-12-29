package com.luv2code.doan.service;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;

import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

public interface ProductService {
    public static final int PRODUCT_PER_PAGE = 9;


    public Product getProductByName(String name);


    public int getCountByCategoryId(Integer categoryId) throws CategoryNotFoundException;

    public int getCount();

    public List<Product> getAllByCategoryId(Integer categoryId, int pageNo, int pageSize, String sortField, String sortDirection) throws CategoryNotFoundException;

    public List<Product> getListProducts( int pageNo, int pageSize, String sortField, String sortDirection);

    public Page<Product> getListProductsSearch( int pageNo, int pageSize, String sortField, String sortDirection, String keyword, Double minPrice, Double maxPrice, List<Integer> listCategoryIds);

    public Page<Product> getListProductsAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword);

    public Product getProductById(Integer id) throws ProductNotFoundException;

    public Product saveProduct(Product product);


    public void deleteProduct(Integer id) throws ProductNotFoundException;

    public void approveProduct(Integer id) throws ProductNotFoundException;

    public Page<Product> listLatestProduct();

    public Page<Product> listBestSellProduct();


    public Double getMaxPrice();

    public Page<String> getSuggest(String keyword);
}
