package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Role;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.repository.RoleRepository;
import com.luv2code.doan.repository.UserRepository;
import com.luv2code.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private org.thymeleaf.spring5.SpringTemplateEngine templateEngine;



    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User saveUserAdmin(User user) {

        return userRepository.save(user);
    }

    @Override
    public User saveEditUser(User user) {
        return userRepository.save(user);
    }




    @Override
    public User getUserByID(int id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();

        }
        catch(NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any user with ID " + id);

        }
    }

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if(user == null) {
            throw new UserNotFoundException("Could not find any user with email " + email);
        }
        return user;
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.getUserByPhone(phone);
    }

    @Override
    public void deleteUser(Integer id) throws UserNotFoundException {
        try {
            User user = userRepository.findById(id).get();
            user.setIsActive(false);
            userRepository.save(user);

        }
        catch(NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
    }

    @Override
    public void approveUser(Integer id) throws UserNotFoundException {
        try {
            User user = userRepository.findById(id).get();
            user.setIsActive(true);
            userRepository.save(user);

        }
        catch(NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
    }

    @Override
    public Page<User> listByPage(Integer pageNum, String keyword, String sortField, String sortDir) {
        Pageable pageable = null;

        if(sortField != null && !sortField.isEmpty()) {
            Sort sort = Sort.by(sortField);
            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
            pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);
        }
        else {
            pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE);
        }

//        if (keyword != null && !keyword.isEmpty()) {
//            return userRepository.findAll(keyword, pageable);
//        }
        return userRepository.findAll(pageable);
    }

    @Override
    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.getUserByEmail(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role getRole(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Username is: " + username );
        User user = userRepository.getUserByEmail(username);
        if(user == null) {
            log.error("User not found!");
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }

        return UserPrincipal.build(user);
    }

    @Override
    public Page<User> getListUsersAdmin(int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
        Pageable pageable = PageRequest.of(pageNo -1, pageSize,sort);
        if(keyword != null) {
            return userRepository.getListUsersAdminWithKeyword(keyword, pageable);
        }
        else {
            return userRepository.getListUsersAdmin(pageable);
        }
    }
}
