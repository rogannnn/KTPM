package com.luv2code.doan.service;

import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.exceptions.BrandNotFoundException;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

public interface BrandService {

    public List<Brand> getAllBrand();

    public Brand getBrandById(Integer id) throws BrandNotFoundException;


    public Brand saveBrand(Brand brand);

    public List<Brand> getListBrands();

    public Page<Brand> getListBrandsAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword);

    public void deleteBrand(Integer id) throws BrandNotFoundException;

    public void approveBrand(Integer id) throws BrandNotFoundException;
}
