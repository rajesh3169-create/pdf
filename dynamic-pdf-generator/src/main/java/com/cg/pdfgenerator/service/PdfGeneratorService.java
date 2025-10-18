package com.cg.pdfgenerator.service;

import com.cg.pdfgenerator.model.PdfGenerationRequest;
import com.cg.pdfgenerator.model.PdfTemplate;
import com.cg.pdfgenerator.processor.*;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PdfGeneratorService {
    
    private final TemplateService templateService;
    private final MultiLanguagePdfService multiLanguagePdfService;
    private final Map<String, ElementProcessor> processors = new HashMap<>();
    
    // Single constructor with both dependencies
    public PdfGeneratorService(TemplateService templateService, 
                               MultiLanguagePdfService multiLanguagePdfService) {
        this.templateService = templateService;
        this.multiLanguagePdfService = multiLanguagePdfService;
        initializeProcessors();
    }
    
    private void initializeProcessors() {
        // Initialize all processors with multi-language support
        TextElementProcessor textProcessor = new TextElementProcessor();
        textProcessor.setMultiLanguagePdfService(multiLanguagePdfService);
        processors.put("TEXT", textProcessor);
        
        ParagraphElementProcessor paragraphProcessor = new ParagraphElementProcessor();
        paragraphProcessor.setMultiLanguagePdfService(multiLanguagePdfService);
        processors.put("PARAGRAPH", paragraphProcessor);
        
        NumberElementProcessor numberProcessor = new NumberElementProcessor();
        numberProcessor.setMultiLanguagePdfService(multiLanguagePdfService);
        processors.put("NUMBER", numberProcessor);
        
        ImageElementProcessor imageProcessor = new ImageElementProcessor();
        imageProcessor.setMultiLanguagePdfService(multiLanguagePdfService);
        processors.put("IMAGE", imageProcessor);
        
        TableElementProcessor tableProcessor = new TableElementProcessor();
        tableProcessor.setMultiLanguagePdfService(multiLanguagePdfService);
        processors.put("TABLE", tableProcessor);
        
        HeaderElementProcessor headerProcessor = new HeaderElementProcessor();
        headerProcessor.setMultiLanguagePdfService(multiLanguagePdfService);
        processors.put("HEADER", headerProcessor);
        
        FooterElementProcessor footerProcessor = new FooterElementProcessor();
        footerProcessor.setMultiLanguagePdfService(multiLanguagePdfService);
        processors.put("FOOTER", footerProcessor);
    }
    
    public byte[] generatePdf(PdfGenerationRequest request) throws Exception {
        log.info("Generating PDF for template: {}", request.getTemplateId());
        
        PdfTemplate template = templateService.getTemplate(request.getTemplateId());
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            
            // Apply page settings
            applyPageSettings(pdfDoc, template.getPageSettings());
            
            Document document = new Document(pdfDoc);
            applyMargins(document, template.getPageSettings());
            
            // Process each element in the template
            for (PdfTemplate.Element element : template.getElements()) {
                processElement(document, element, request.getData());
            }
            
            document.close();
            log.info("PDF generated successfully");
            
            return baos.toByteArray();
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
            case "EXECUTIVE" -> PageSize.EXECUTIVE;
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