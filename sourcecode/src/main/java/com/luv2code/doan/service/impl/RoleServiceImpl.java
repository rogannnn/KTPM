package com.luv2code.doan.service.impl;


import com.luv2code.doan.entity.Role;
import com.luv2code.doan.exceptions.RoleNotFoundException;
import com.luv2code.doan.repository.RoleRepository;
import com.luv2code.doan.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public Role getRoleByID(int id) throws RoleNotFoundException {
        try {
            Role role = roleRepository.findById(id).get();
            return role;
        }
        catch(NoSuchElementException ex) {
            throw new RoleNotFoundException("Could not find any role with ID " + id);

        }
    }

    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }

}
