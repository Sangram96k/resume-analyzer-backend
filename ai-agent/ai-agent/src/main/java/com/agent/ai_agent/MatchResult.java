package com.agent.ai_agent;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "match_results")
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;
    private String jobTitle;
    private int matchScore;
    private String hiringRecommendation;

    @Column(length = 1000)
    private String skillGaps;

    @Column(length = 1000)
    private String matchingSkills;

    @Column(length = 500)
    private String summaryRemark;

    private LocalDateTime evaluatedAt;
}