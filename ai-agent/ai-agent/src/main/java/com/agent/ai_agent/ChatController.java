package com.agent.ai_agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class ChatController {

    private final ChatClient chatClient;
    private final ResumeService resumeService;
    private final JDMatcherService jdMatcherService;
    private final MatchHistoryService matchHistoryService;  // NEW

    public ChatController(ChatClient.Builder builder, ResumeService resumeService,
                          JDMatcherService jdMatcherService, MatchHistoryService matchHistoryService) {
        this.chatClient = builder.build();
        this.resumeService = resumeService;
        this.jdMatcherService = jdMatcherService;
        this.matchHistoryService = matchHistoryService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatClient.prompt().user(message).call().content();
    }

    @PostMapping("/verify-context")
    public String verifyContext(@RequestParam("file") MultipartFile file) {
        try {
            String resumeText = resumeService.extractText(file);
            String tinyContext = resumeText.length() > 20000 ? resumeText.substring(0, 20000) : resumeText;

            String combinedPrompt = String.format("""
                Instructions: You are a document analyzer.
                Below is a snippet of a candidate's resume.

                --- RESUME SNIPPET ---
                %s
                --- END OF SNIPPET ---

                Task: Based on the snippet provided above, what is the candidate's name and their primary degree, and skills and how many projects did he have in context?, and what is candidate capable of
                """, tinyContext);

            return chatClient.prompt().user(combinedPrompt).call().content();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Main match endpoint — now saves result to MySQL
    @PostMapping(value = "/match-jd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> matchJobDescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jd") String jd) {
        try {
            String resumeText = resumeService.extractText(file);
            String tinyResume = resumeText.length() > 3000 ? resumeText.substring(0, 3000) : resumeText;
            String compactJd  = extractKeyJdLines(jd);

            String result = jdMatcherService.match(tinyResume, compactJd);

            // AI extracts candidateName and jobTitle automatically from the JSON
            matchHistoryService.save(result);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Get match history by candidate name
    @GetMapping("/history/candidate/{name}")
    public List<MatchResult> getHistoryByCandidate(@PathVariable String name) {
        return matchHistoryService.getByCandidate(name);
    }

    // Get match history by job title
    @GetMapping("/history/job/{jobTitle}")
    public List<MatchResult> getHistoryByJob(@PathVariable String jobTitle) {
        return matchHistoryService.getByJobTitle(jobTitle);
    }

    // Get top candidates above a score
    @GetMapping("/history/top/{minScore}")
    public List<MatchResult> getTopCandidates(@PathVariable int minScore) {
        return matchHistoryService.getTopCandidates(minScore);
    }

    // Get all history
    @GetMapping("/history/all")
    public List<MatchResult> getAllHistory() {
        return matchHistoryService.getAll();
    }

    private String extractKeyJdLines(String jd) {
        String[] keywords = {
                "must", "require", "mandatory", "essential", "skill", "experience",
                "qualification", "education", "degree", "responsible", "knowledge",
                "proficient", "expertise", "certif", "tool", "technology", "framework",
                "language", "year", "minimum", "preferred", "ability", "strong"
        };

        StringBuilder result = new StringBuilder();
        for (String line : jd.split("\\n")) {
            String lower = line.toLowerCase().trim();
            if (lower.isEmpty()) continue;
            for (String keyword : keywords) {
                if (lower.contains(keyword)) {
                    result.append(line.trim()).append("\n");
                    break;
                }
            }
        }

        String extracted = result.toString().trim();
        if (extracted.length() < 100) {
            return jd.length() > 3000 ? jd.substring(0, 3000) : jd;
        }
        return extracted.length() > 3000 ? extracted.substring(0, 3000) : extracted;
    }
}