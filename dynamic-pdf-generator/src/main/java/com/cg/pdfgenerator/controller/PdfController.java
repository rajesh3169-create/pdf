
// ============================= CONTROLLERS =============================
// File: controller/PdfController.java
package com.cg.pdfgenerator.controller;

import com.cg.pdfgenerator.model.PdfGenerationRequest;
import com.cg.pdfgenerator.model.PdfTemplate;
import com.cg.pdfgenerator.service.PdfGeneratorService;
import com.cg.pdfgenerator.service.TemplateStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
@Slf4j
public class PdfController {
    
    private final PdfGeneratorService pdfGeneratorService;
    private final TemplateStore templateStore;
    
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfGenerationRequest request) {
        try {
            log.info("PDF generation request received for template: {}", request.getTemplateId());
            
            byte[] pdfBytes = pdfGeneratorService.generatePdf(request);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String fileName = request.getOutputFileName() != null 
                ? request.getOutputFileName() 
                : "document.pdf";
            headers.setContentDispositionFormData("attachment", fileName);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/template")
    public ResponseEntity<String> saveTemplate(@RequestBody PdfTemplate template) {
        try {
            templateStore.saveTemplate(template);
            return ResponseEntity.ok("Template saved: " + template.getTemplateId());
        } catch (Exception e) {
            log.error("Error saving template", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/template/{templateId}")
    public ResponseEntity<PdfTemplate> getTemplate(@PathVariable String templateId) {
        try {
            PdfTemplate template = templateStore.getTemplate(templateId);
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            log.error("Error retrieving template", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @DeleteMapping("/template/{templateId}")
    public ResponseEntity<String> deleteTemplate(@PathVariable String templateId) {
        try {
            templateStore.removeTemplate(templateId);
            return ResponseEntity.ok("Template deleted: " + templateId);
        } catch (Exception e) {
            log.error("Error deleting template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}