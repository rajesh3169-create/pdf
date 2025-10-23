package com.cg.pdfgenerator.service;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TemplateStore {

    private final Map<String, PdfTemplate> templates = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveTemplate(PdfTemplate template) {
        if (template.getTemplateId() == null || template.getTemplateId().isEmpty()) {
            throw new IllegalArgumentException("Template ID is required");
        }
        templates.put(template.getTemplateId(), template);
        log.info("Template saved: {}", template.getTemplateId());
    }

    public PdfTemplate getTemplate(String templateId) {
        PdfTemplate template = templates.get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }
        return template;
    }

    public void removeTemplate(String templateId) {
        templates.remove(templateId);
        log.info("Template removed: {}", templateId);
    }

    public boolean templateExists(String templateId) {
        return templates.containsKey(templateId);
    }
}
