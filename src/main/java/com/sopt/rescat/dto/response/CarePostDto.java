package com.sopt.rescat.dto.response;

import com.sopt.rescat.domain.photo.CarePostPhoto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CarePostDto {
    private Long idx;
    private String name;
    private String contents;
    private CarePostPhoto photo;
    // 0: 입양, 1: 임시보호
    private Integer type;
    private int viewCount;
    private LocalDateTime createdAt;
}