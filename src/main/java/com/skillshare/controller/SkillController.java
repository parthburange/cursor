package com.skillshare.controller;

import com.skillshare.dto.SkillRequest;
import com.skillshare.dto.SkillResponse;
import com.skillshare.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skill management APIs")
public class SkillController {
    
    private final SkillService skillService;
    
    @PostMapping("/add")
    @Operation(summary = "Add a new skill", description = "Adds a new skill for the authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SkillResponse> addSkill(
            @Valid @RequestBody SkillRequest request,
            Authentication authentication
    ) {
        SkillResponse response = skillService.addSkill(request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all public skills", description = "Retrieves all publicly available skills")
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        List<SkillResponse> skills = skillService.getAllPublicSkills();
        return ResponseEntity.ok(skills);
    }
    
    @GetMapping("/my")
    @Operation(summary = "Get user's skills", description = "Retrieves skills for the authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<SkillResponse>> getMySkills(Authentication authentication) {
        List<SkillResponse> skills = skillService.getSkillsByUser(authentication.getName());
        return ResponseEntity.ok(skills);
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get skills by category", description = "Retrieves public skills by category")
    public ResponseEntity<List<SkillResponse>> getSkillsByCategory(@PathVariable String category) {
        List<SkillResponse> skills = skillService.getSkillsByCategory(category);
        return ResponseEntity.ok(skills);
    }
}