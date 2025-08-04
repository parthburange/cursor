package com.skillshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private LocalDateTime createdAt;
    private String username;
    private boolean isPublic;
}