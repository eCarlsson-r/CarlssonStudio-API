package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.entity.FoundationEntity;
import com.carlssonstudio.api.repository.FoundationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoundationService {

    private final FoundationRepository foundationRepository;

    public List<FoundationResponse> findAll() {
        return foundationRepository.findAll().stream()
                .map(this::map).collect(Collectors.toList());
    }

    public List<FoundationResponse> findActive() {
        return foundationRepository.findByActiveTrue().stream()
                .map(this::map).collect(Collectors.toList());
    }

    @Transactional
    public FoundationResponse create(FoundationRequest req) {
        if (foundationRepository.existsBySlug(req.getSlug())) {
            throw new RuntimeException(
                "Foundation slug already exists: " + req.getSlug());
        }
        FoundationEntity entity = FoundationEntity.builder()
                .slug(req.getSlug())
                .name(req.getName())
                .industry(req.getIndustry())
                .relatedIndustries(req.getRelatedIndustries())
                .buildTypes(req.getBuildTypes())
                .problems(req.getProblems())
                .features(req.getFeatures())
                .description(req.getDescription())
                .active(false)  // new foundations start inactive
                .build();
        return map(foundationRepository.save(entity));
    }

    @Transactional
    public FoundationResponse update(Long id,
                                     FoundationRequest req) {
        FoundationEntity entity = foundationRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Foundation not found: " + id));

        entity.setName(req.getName());
        entity.setIndustry(req.getIndustry());
        entity.setRelatedIndustries(req.getRelatedIndustries());
        entity.setBuildTypes(req.getBuildTypes());
        entity.setProblems(req.getProblems());
        entity.setFeatures(req.getFeatures());
        entity.setDescription(req.getDescription());

        return map(foundationRepository.save(entity));
    }

    @Transactional
    public FoundationResponse toggleActive(Long id) {
        FoundationEntity entity = foundationRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                    "Foundation not found: " + id));
        entity.setActive(!entity.isActive());
        return map(foundationRepository.save(entity));
    }

    private FoundationResponse map(FoundationEntity e) {
        return FoundationResponse.builder()
                .id(e.getId())
                .slug(e.getSlug())
                .name(e.getName())
                .industry(e.getIndustry())
                .relatedIndustries(e.getRelatedIndustries())
                .buildTypes(e.getBuildTypes())
                .problems(e.getProblems())
                .features(e.getFeatures())
                .description(e.getDescription())
                .active(e.isActive())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}