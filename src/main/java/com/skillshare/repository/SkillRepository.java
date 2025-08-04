package com.skillshare.repository;

import com.skillshare.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByIsPublicTrue();
    List<Skill> findByUserUsername(String username);
    List<Skill> findByCategory(String category);
}