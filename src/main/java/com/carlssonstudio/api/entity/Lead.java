package com.carlssonstudio.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 150)
    private String email;

    @Column(length = 25)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeadSource source = LeadSource.WEBSITE;

    @Column(length = 100)
    private String company;

    @Column(name = "company_size", length = 10)
    private String companySize;

    @Column(nullable = false, length = 50)
    private String industry;

    @Column(name = "build_type", length = 50)
    private String buildType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> problems;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> features;

    /**
     * business_status is NOT NULL in the schema with no DB default, so a
     * builder that forgets it fails the insert and loses the lead. Both
     * submit paths set this explicitly; the default here is a safety net so
     * a future caller can never drop a lead over a missing enum.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BusinessStatus businessStatus = BusinessStatus.RUNNING;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeadRecommendation> recommendations;

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