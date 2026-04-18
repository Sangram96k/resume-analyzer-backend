package com.agent.ai_agent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    // Get all results for a specific candidate
    List<MatchResult> findByCandidateNameIgnoreCase(String candidateName);

    // Get all results for a specific job title
    List<MatchResult> findByJobTitleIgnoreCase(String jobTitle);

    // Get top matches above a certain score
    List<MatchResult> findByMatchScoreGreaterThanEqualOrderByMatchScoreDesc(int score);
}