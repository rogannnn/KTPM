package com.luv2code.doan.controller;

import com.luv2code.doan.dto.CategoryDto;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.response.ListCategoryResponse;
import com.luv2code.doan.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryRestController {
    private final CategoryService categoryService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ListCategoryResponse> getListCategories(HttpServletRequest request) {


        List<Category> listCategories = categoryService.getListCategories();

        List<CategoryDto> listCategoryDto = new ArrayList<>();

        for(Category c : listCategories) {
            listCategoryDto.add(new CategoryDto(c));
        }
        ListCategoryResponse result = new ListCategoryResponse(1, "Get all category successfully!",
                request.getMethod(),new Date().getTime(),HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                listCategoryDto,null, null);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ListCategoryResponse> getCategoryPerPage(@RequestParam(value = "pageNo", required = false) Optional<Integer> pPageNo,
                                                                   @RequestParam(value = "pageSize", required = false) Optional<Integer> pPageSize,
                                                                   HttpServletRequest request) {
        int pageNo = 1;
        int pageSize = 6;

        if (pPageNo.isPresent()) {
            pageNo = pPageNo.get();
        }
        if (pPageSize.isPresent()) {
            pageSize = pPageSize.get();
        }

        int totalPage = 0;
        List<Category> listCategories = new ArrayList<>();

        Page page = categoryService.getCategoryPerPage(pageNo, pageSize);

        listCategories = page.getContent();
        totalPage = page.getTotalPages();

        List<CategoryDto> listCategoryDto = new ArrayList<>();

        for(Category c : listCategories) {
            listCategoryDto.add(new CategoryDto(c));
        }
        ListCategoryResponse result = new ListCategoryResponse(1, "Get all category successfully!",
                request.getMethod(),new Date().getTime(),HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                listCategoryDto, totalPage, pageNo);

        return new ResponseEntity(result, HttpStatus.OK);
    }
}
