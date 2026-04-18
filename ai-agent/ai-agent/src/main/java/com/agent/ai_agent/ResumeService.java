package com.agent.ai_agent;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    public String extractText(MultipartFile file) {
        try {
            TikaDocumentReader reader = new TikaDocumentReader(file.getResource());
            List<Document> documents = reader.read();

            // Using .getText() to avoid the red-highlight error
            return documents.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "Error parsing file: " + e.getMessage();
        }
    }
}