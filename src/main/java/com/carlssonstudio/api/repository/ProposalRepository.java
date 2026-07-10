package com.carlssonstudio.api.repository;

import com.carlssonstudio.api.entity.Proposal;
import com.carlssonstudio.api.entity.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findByLeadId(Long leadId);
    List<Proposal> findByStatus(ProposalStatus status);
    Optional<Proposal> findByLeadIdAndFoundationSlug(Long leadId, String foundationSlug);
}