package com.youtube.be.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    Object uploadFile(MultipartFile file, String path);
}
