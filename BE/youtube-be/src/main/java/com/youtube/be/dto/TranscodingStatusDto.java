package com.youtube.be.dto;

import lombok.Getter;

@Getter
public class TranscodingStatusDto {
    private String contentId;
    private boolean success;
    private String transcodedPath;
}
