package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.Lead;
import com.carlssonstudio.api.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByStatus(LeadStatus status);
    List<Lead> findByIndustry(String industry);
    boolean existsByEmail(String email);
}