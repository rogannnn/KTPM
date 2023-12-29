package com.luv2code.doan.controller;

import com.luv2code.doan.exceptions.DuplicateException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", new Date().getTime());
        body.put("status", status.getReasonPhrase());

        //Get all errors
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();

        body.put("result", 0);
        body.put("code", HttpStatus.BAD_REQUEST.value());
        if(errors.size() != 0) {
            body.put("msg", errors.get(0).getDefaultMessage());
        }
        else {
            body.put("msg", "");
        }
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleError(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", new Date().getTime());
        body.put("status", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("code", HttpStatus.BAD_REQUEST.value());
        body.put("msg", ex.getMessage());
        body.put("result", 0);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }
}
