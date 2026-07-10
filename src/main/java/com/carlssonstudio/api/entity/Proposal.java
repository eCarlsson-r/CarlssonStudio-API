package com.carlssonstudio.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @Column(name = "foundation_slug", nullable = false, length = 50)
    private String foundationSlug;

    @Column(name = "foundation_name", nullable = false, length = 100)
    private String foundationName;

    @Column(name = "match_score", nullable = false)
    private Integer matchScore;

    @Column(name = "timeline_weeks", nullable = false)
    private Integer timelineWeeks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalComplexity complexity;

    @Column(name = "file_path", length = 255)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProposalStatus status = ProposalStatus.DRAFT;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}