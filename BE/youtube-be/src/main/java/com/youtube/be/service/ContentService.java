package com.youtube.be.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ContentService {

    Object uploadVideo(MultipartFile file, String name) throws IOException;
}
