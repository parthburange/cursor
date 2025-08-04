package com.skillshare.service;

import com.skillshare.dto.SkillRequest;
import com.skillshare.dto.SkillResponse;
import com.skillshare.entity.Skill;
import com.skillshare.entity.User;
import com.skillshare.repository.SkillRepository;
import com.skillshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    
    public SkillResponse addSkill(SkillRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setDescription(request.getDescription());
        skill.setCategory(request.getCategory());
        skill.setPublic(request.isPublic());
        skill.setUser(user);
        
        Skill savedSkill = skillRepository.save(skill);
        
        return convertToResponse(savedSkill);
    }
    
    public List<SkillResponse> getAllPublicSkills() {
        return skillRepository.findByIsPublicTrue()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SkillResponse> getSkillsByUser(String username) {
        return skillRepository.findByUserUsername(username)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SkillResponse> getSkillsByCategory(String category) {
        return skillRepository.findByCategory(category)
                .stream()
                .filter(Skill::isPublic)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private SkillResponse convertToResponse(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getDescription(),
                skill.getCategory(),
                skill.getCreatedAt(),
                skill.getUser().getUsername(),
                skill.isPublic()
        );
    }
}