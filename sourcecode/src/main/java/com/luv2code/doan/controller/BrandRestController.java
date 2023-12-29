package com.luv2code.doan.controller;

import com.luv2code.doan.dto.BrandDto;
import com.luv2code.doan.dto.CategoryDto;
import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.response.ListBrandResponse;
import com.luv2code.doan.response.ListCategoryResponse;
import com.luv2code.doan.service.BrandService;
import com.luv2code.doan.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
@Slf4j
public class BrandRestController {
    private final BrandService brandService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ListBrandResponse> getListBrand(HttpServletRequest request) {
        List<Brand> listBrands = brandService.getListBrands();

        List<BrandDto> listBrandDto = new ArrayList<>();

        for(Brand b : listBrands) {
            listBrandDto.add(new BrandDto(b));
        }
        ListBrandResponse result = new ListBrandResponse(1, "Get all brand successfully!",
                request.getMethod(),new Date().getTime(), HttpStatus.CREATED.getReasonPhrase(), HttpStatus.CREATED.value(),
                listBrandDto );

        return new ResponseEntity(result, HttpStatus.CREATED);
    }
}