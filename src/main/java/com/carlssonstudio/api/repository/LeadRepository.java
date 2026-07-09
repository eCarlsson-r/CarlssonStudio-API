package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.Lead;
import com.carlssonstudio.api.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByStatus(LeadStatus status);
    List<Lead> findByIndustry(String industry);
    boolean existsByEmail(String email);
}