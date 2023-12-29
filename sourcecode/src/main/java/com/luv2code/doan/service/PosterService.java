package com.luv2code.doan.service;

import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Poster;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.PosterNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.PosterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

public interface PosterService {

    public List<Poster> getListPosters();


    public Poster getPosterById(Integer id) throws PosterNotFoundException;

    public Poster savePoster(Poster poster);

    public void deletePoster(Integer id) throws PosterNotFoundException;

    public void approvePoster(Integer id) throws PosterNotFoundException;

    public Page<Poster> getListPostersAdmin(int pageNo, int pageSize, String sortField, String sortDirection, String keyword);

}
