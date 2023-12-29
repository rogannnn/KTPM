package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Poster;
import com.luv2code.doan.exceptions.PosterNotFoundException;
import com.luv2code.doan.repository.PosterRepository;
import com.luv2code.doan.service.PosterService;
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

@Service
public class PosterServiceImpl implements PosterService {
    private final Logger log = LoggerFactory.getLogger(PosterServiceImpl.class);

    @Autowired
    private PosterRepository posterRepository;


    public List<Poster> getListPosters(){
        return posterRepository.getListPosters();
    }


    public Poster getPosterById(Integer id) throws PosterNotFoundException {
        try {
            Poster poster = posterRepository.findById(id).get();
            return poster;
        }
        catch(NoSuchElementException ex) {
            throw new PosterNotFoundException("Could not find any poster with ID " + id);

        }
    }

    public Poster savePoster(Poster poster) {
        return posterRepository.save(poster);
    }

    public void deletePoster(Integer id) throws PosterNotFoundException {
        try {
            Poster poster = posterRepository.findById(id).get();
            poster.setIsActive(false);
            posterRepository.save(poster);

        }
        catch(NoSuchElementException ex) {
            throw new PosterNotFoundException("Could not find any poster with ID " + id);
        }
    }

    public void approvePoster(Integer id) throws PosterNotFoundException {
        try {
            Poster poster = posterRepository.findById(id).get();
            poster.setIsActive(true);
            posterRepository.save(poster);

        }
        catch(NoSuchElementException ex) {
            throw new PosterNotFoundException("Could not find any poster with ID " + id);
        }
    }

    public Page<Poster> getListPostersAdmin(int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        if(keyword != null) {
            return posterRepository.getListPostersAdminWithKeyword(keyword, pageable);
        }
        else {
            return posterRepository.getListPostersAdmin(pageable);
        }
    }

}
