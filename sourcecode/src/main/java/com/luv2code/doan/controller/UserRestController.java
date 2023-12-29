package com.luv2code.doan.controller;

import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {
    private final UserService userService;

    @RequestMapping(value = "/id", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
        User user = null;
        try {
            user = userService.getUserByID(id);
        } catch (UserNotFoundException e) {

        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
