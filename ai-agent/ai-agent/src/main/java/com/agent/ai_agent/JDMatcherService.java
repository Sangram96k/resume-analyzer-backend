package com.agent.ai_agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class JDMatcherService {

    private final ChatClient chatClient;

    public JDMatcherService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String match(String resumeText, String jdText) {

        String prompt = String.format("""
You are a strict Technical Recruiter and HR expert in Pune, India.
Compare the resume against the Job Description below.

--- CANDIDATE RESUME ---
%s

--- JOB DESCRIPTION ---
%s

---

Scoring Rules (Total = 100 points):
1. Must-Have Skills   → 40 pts  (If the #1 required skill is ABSENT, give MAX 5 pts here)
2. Technical Skills   → 25 pts  (Languages, tools, frameworks mentioned in JD)
3. Experience         → 20 pts  (Years of experience, domain, responsibilities)
4. Education          → 10 pts  (Degree and field as required in JD)
5. Soft Skills        →  5 pts  (Certifications, leadership, communication)

Strict Rules:
- Score ONLY what is explicitly written in the resume. No assumptions.
- If the primary must-have skill is completely missing, cap TOTAL score at 25.
- Fresher applying for senior role, cap TOTAL score at 30.

Return ONLY this JSON, no extra text:
{
  "candidateName": "<full name from resume, Unknown if not found>",
  "jobTitle": "<job title from JD, Unknown if not found>",
  "matchScore": <0-100>,
  "hiringRecommendation": "<STRONG_FIT | MODERATE_FIT | WEAK_FIT | NOT_RECOMMENDED>",
  "breakdown": {
    "mustHaveSkills": <0-40>,
    "technicalSkills": <0-25>,
    "experience": <0-20>,
    "education": <0-10>,
    "softSkills": <0-5>
  },
  "skillGaps": ["gap1", "gap2", "gap3"],
  "matchingSkills": ["skill1", "skill2"],
  "summaryRemark": "<one line summary>"
}
""", resumeText, jdText);

        String response = chatClient.prompt().user(prompt).call().content();

        return response
                .replaceAll("(?s)```json\\s*", "")
                .replaceAll("(?s)```\\s*", "")
                .trim();
    }
}