package com.financetracker.dto;

import com.financetracker.entity.Category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    @NotNull(message = "Category type is required")
    private CategoryType type;

    private String colorHex = "#007bff";
}