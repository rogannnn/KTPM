package com.luv2code.doan.service;


import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Role;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.RoleNotFoundException;
import com.luv2code.doan.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


public interface RoleService {


    public Role saveRole(Role role);

    public Role getRoleByID(int id) throws RoleNotFoundException;

    public List<Role> findAllRole();

}
