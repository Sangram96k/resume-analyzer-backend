package com.agent.ai_agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchHistoryService {

    private final MatchResultRepository repository;
    private final ObjectMapper objectMapper;

    public MatchHistoryService(MatchResultRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    // Called after every match — parses the JSON result and saves to MySQL
    public void save(String jsonResult) {
        try {
            JsonNode node = objectMapper.readTree(jsonResult);

            MatchResult result = new MatchResult();
            result.setCandidateName(node.has("candidateName") ? node.get("candidateName").asText() : "Unknown");
            result.setJobTitle(node.has("jobTitle") ? node.get("jobTitle").asText() : "Unknown");
            result.setMatchScore(node.get("matchScore").asInt());
            result.setHiringRecommendation(node.get("hiringRecommendation").asText());
            result.setSummaryRemark(node.get("summaryRemark").asText());
            result.setEvaluatedAt(LocalDateTime.now());

            // Convert arrays to comma separated strings for simple storage
            if (node.has("skillGaps")) {
                result.setSkillGaps(node.get("skillGaps").toString());
            }
            if (node.has("matchingSkills")) {
                result.setMatchingSkills(node.get("matchingSkills").toString());
            }

            repository.save(result);

        } catch (Exception e) {
            // Don't break the main flow if save fails — just print
            System.err.println("Failed to save match result: " + e.getMessage());
        }
    }

    // Get all history for a candidate
    public List<MatchResult> getByCandidate(String candidateName) {
        return repository.findByCandidateNameIgnoreCase(candidateName);
    }

    // Get all history for a job title
    public List<MatchResult> getByJobTitle(String jobTitle) {
        return repository.findByJobTitleIgnoreCase(jobTitle);
    }

    // Get all candidates who scored above given score
    public List<MatchResult> getTopCandidates(int minScore) {
        return repository.findByMatchScoreGreaterThanEqualOrderByMatchScoreDesc(minScore);
    }

    // Get all history
    public List<MatchResult> getAll() {
        return repository.findAll();
    }
}