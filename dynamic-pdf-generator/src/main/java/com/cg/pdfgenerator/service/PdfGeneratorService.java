
// File: service/PdfGeneratorService.java
package com.cg.pdfgenerator.service;

import com.cg.pdfgenerator.model.PdfGenerationRequest;
import com.cg.pdfgenerator.model.PdfTemplate;
import com.cg.pdfgenerator.processor.*;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGeneratorService {

    private final TemplateStore templateStore;
    private final Map<String, ElementProcessor> processors = new HashMap<>();

    @jakarta.annotation.PostConstruct
    private void initializeProcessors() {
        processors.put("TEXT", new TextElementProcessor());
        processors.put("PARAGRAPH", new ParagraphElementProcessor());
        processors.put("NUMBER", new NumberElementProcessor());
        processors.put("TABLE", new TableElementProcessor());  // ← FIXED: Added TableElementProcessor

        processors.put("IMAGE", new ImageElementProcessor());
        processors.put("HEADER", new HeaderElementProcessor());
        processors.put("FOOTER", new FooterElementProcessor());
    }
    public byte[] generatePdf(PdfGenerationRequest request) throws Exception {
        log.info("Generating PDF for template: {}", request.getTemplateId());
        
        PdfTemplate template = templateStore.getTemplate(request.getTemplateId());
        Map<String, Object> data = request.getData() != null ? request.getData() : new HashMap<>();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            
            applyPageSettings(pdfDoc, template.getPageSettings());
            
            Document document = new Document(pdfDoc);
            applyMargins(document, template.getPageSettings());
            
            if (template.getElements() != null) {
                for (PdfTemplate.Element element : template.getElements()) {
                    processElement(document, element, data);
                }
            }
            
            document.close();
            log.info("PDF generated successfully");
            
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw e;
        }
    }
    
    private void applyPageSettings(PdfDocument pdfDoc, PdfTemplate.PageSettings settings) {
        if (settings == null) {
            return;
        }
        
        PageSize pageSize = getPageSize(settings.getSize());
        
        if ("LANDSCAPE".equalsIgnoreCase(settings.getOrientation())) {
            pageSize = pageSize.rotate();
        }
        
        pdfDoc.setDefaultPageSize(pageSize);
    }
    
    private void applyMargins(Document document, PdfTemplate.PageSettings settings) {
        if (settings == null) {
            return;
        }
        
        float top = settings.getMarginTop() != null ? settings.getMarginTop() : 36f;
        float bottom = settings.getMarginBottom() != null ? settings.getMarginBottom() : 36f;
        float left = settings.getMarginLeft() != null ? settings.getMarginLeft() : 36f;
        float right = settings.getMarginRight() != null ? settings.getMarginRight() : 36f;
        
        document.setMargins(top, right, bottom, left);
    }
    
    private PageSize getPageSize(String size) {
        if (size == null) {
            return PageSize.A4;
        }
        
        return switch (size.toUpperCase()) {
            case "LETTER" -> PageSize.LETTER;
            case "LEGAL" -> PageSize.LEGAL;
            case "A3" -> PageSize.A3;
            case "A5" -> PageSize.A5;
            case "TABLOID" -> PageSize.TABLOID;
            default -> PageSize.A4;
        };
    }
    
    private void processElement(Document document, PdfTemplate.Element element, 
                                Map<String, Object> data) throws Exception {
        ElementProcessor processor = processors.get(element.getType().toUpperCase());
        
        if (processor == null) {
            log.warn("No processor found for element type: {}", element.getType());
            return;
        }
        
        processor.process(document, element, data);
    }
}