package com.luv2code.doan.service.impl;


import com.luv2code.doan.entity.Category;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.repository.CategoryRepository;
import com.luv2code.doan.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryServiceImpl implements CategoryService {

    public static final int CATEGORY_PER_PAGE = 9;

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getCategoryByName(String name) {
        Category category = categoryRepository.getCategoryByName(name);
        return category;
    }


    public Page<Category> listByPage(Integer pageNum, String keyword, String sortField, String sortDir) {
        Pageable pageable = null;

        if(sortField != null && !sortField.isEmpty()) {
            Sort sort = Sort.by(sortField);
            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
            pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE, sort);
        }
        else {
            pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return categoryRepository.findAll(keyword, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    public Category getCategoryById(Integer id) throws CategoryNotFoundException {
        try {
            Category category = categoryRepository.findById(id).get();
            return category;
        }
        catch(NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);

        }
    }

    public Page<Category> getCategoryPerPage(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return categoryRepository.getCategoryPerPage(pageable);
    }

    public Page<Category> getListCategoriesAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        if(keyword != null) {
            return categoryRepository.getListCategoriesAdminWithKeyword(keyword, pageable);
        }
        else {
            return categoryRepository.getListCategoriesAdmin(pageable);
        }
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }


    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        try {
            Category category = categoryRepository.findById(id).get();
            category.setActive(false);
            categoryRepository.save(category);

        }
        catch(NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }

    public void approveCategory(Integer id) throws CategoryNotFoundException {
        try {
            Category category = categoryRepository.findById(id).get();
            category.setActive(true);
            categoryRepository.save(category);

        }
        catch(NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }


    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    public List<Category> getListCategories() {
        return categoryRepository.getListCategories();
    }

    public List<Category> getTop5CategoryBestSell(){
        return  categoryRepository.top5CategoryBestSell();
    }
}
