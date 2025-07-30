package com.youtube.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscodingMessageDto {
    private String rawFilePath;
    private String destinationPath;
    private String contentId;
}
