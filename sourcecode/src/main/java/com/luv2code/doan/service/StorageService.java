package com.luv2code.doan.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;
import java.util.Objects;

public interface StorageService {
    public String upload(MultipartFile multipartFile) throws IOException;
    public Map delete(String id) throws IOException;

}
