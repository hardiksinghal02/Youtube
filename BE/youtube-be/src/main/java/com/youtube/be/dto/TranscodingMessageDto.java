package com.youtube.be.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TranscodingMessageDto {
    private String rawFilePath;
    private String destinationPath;
}
