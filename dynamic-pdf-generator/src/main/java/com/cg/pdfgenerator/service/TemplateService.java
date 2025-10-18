package com.cg.pdfgenerator.service;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TemplateService {
    
    private final Map<String, PdfTemplate> templates = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void loadTemplate(String templateId, String jsonFilePath) throws IOException {
        File file = new File(jsonFilePath);
        PdfTemplate template = objectMapper.readValue(file, PdfTemplate.class);
        template.setTemplateId(templateId);
        templates.put(templateId, template);
        log.info("Template loaded: {}", templateId);
    }
    
    public void saveTemplate(PdfTemplate template) {
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