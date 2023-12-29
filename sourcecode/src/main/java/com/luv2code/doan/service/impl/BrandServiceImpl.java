package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.exceptions.BrandNotFoundException;
import com.luv2code.doan.repository.BrandRepository;
import com.luv2code.doan.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandServiceImpl implements BrandService {
    public static final int BRAND_PER_PAGE = 9;
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrand()
    {
        return brandRepository.findAll();
    }



    public Brand getBrandById(Integer id) throws BrandNotFoundException {
        try {
            Brand brand = brandRepository.findById(id).get();
            return brand;

        }
        catch(NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);

        }
    }


    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public List<Brand> getListBrands(){
        return brandRepository.getListBrands();
    }

    public Page<Brand> getListBrandsAdmin( int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        if(keyword != null) {
            return brandRepository.getListBrandsAdminWithKeyword(keyword, pageable);
        }
        else {
            return brandRepository.getListBrandsAdmin(pageable);
        }
    }

    public void deleteBrand(Integer id) throws BrandNotFoundException {
        try {
            Brand brand = brandRepository.findById(id).get();
            brand.setActive(false);
            brandRepository.save(brand);

        }
        catch(NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
    }

    public void approveBrand(Integer id) throws BrandNotFoundException {
        try {
            Brand brand = brandRepository.findById(id).get();
            brand.setActive(true);
            brandRepository.save(brand);

        }
        catch(NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
    }
}
