package com.luv2code.doan.service;


import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

public interface CategoryService {

    public Category getCategoryByName(String name);

    public Page<Category> listByPage(Integer pageNum, String keyword, String sortField, String sortDir);

    public Category getCategoryById(Integer id) throws CategoryNotFoundException;
    public Page<Category> getCategoryPerPage(Integer pageNum, Integer pageSize);

    public Page<Category> getListCategoriesAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword);

    public Category saveCategory(Category category);


    public void deleteCategory(Integer id) throws CategoryNotFoundException;

    public void approveCategory(Integer id) throws CategoryNotFoundException;


    public List<Category> findAllCategory();

    public List<Category> getListCategories();

    public List<Category> getTop5CategoryBestSell();
}
