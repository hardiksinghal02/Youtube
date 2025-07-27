package com.youtube.be.controllers;

import com.youtube.be.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping(value = "/video/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFileToBackend(
            @RequestPart("file") MultipartFile file,
            @RequestPart("name") String name
    ) {
        try {

            return contentService.uploadVideo(file, name);

        } catch (Exception e) {
            return "Error";
        }
    }
}
