package com.luv2code.doan.service;

import com.luv2code.doan.entity.Role;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public interface UserService {
    public static final int USER_PER_PAGE = 9;


    public User saveUser(User user);

    public User saveUserAdmin(User user);

    public User saveEditUser(User user);


    public User getUserByID(int id) throws UserNotFoundException;

    public User getUserByEmail(String email) throws UserNotFoundException;

    public User getUserByPhone(String phone);


    public Page<User> listByPage(Integer pageNum, String keyword, String sortField, String sortDir);

    public List<Role> listRoles();

    public Boolean existsByEmail(String email);
    public Boolean existsByPhone(String phone);

    void addRoleToUser(String username, String roleName);
    Role saveRole(Role role);
    Role getRole(String roleName);

    public Page<User> getListUsersAdmin(int pageNo, int pageSize, String sortField, String sortDirection, String keyword);

    public void deleteUser(Integer id) throws UserNotFoundException;
    public void approveUser(Integer id) throws UserNotFoundException;

}
