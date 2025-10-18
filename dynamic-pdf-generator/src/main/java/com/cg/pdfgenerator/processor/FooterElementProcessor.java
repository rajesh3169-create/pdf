package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.Map;

public class FooterElementProcessor extends BaseElementProcessor {
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String content = resolveContent(element.getContent(), data);
        
        // Support for page numbers
        if (content.contains("{{pageNumber}}")) {
            content = content.replace("{{pageNumber}}", String.valueOf(document.getPdfDocument().getNumberOfPages()));
        }
        
        Paragraph footer = new Paragraph(content);
        
        // Default footer style
        float fontSize = element.getStyle() != null && element.getStyle().getFontSize() != null 
            ? element.getStyle().getFontSize() : 10f;
        footer.setFontSize(fontSize);
        
        if (element.getPosition() != null) {
            if (element.getPosition().getAlignment() != null) {
                footer.setTextAlignment(getTextAlignment(element.getPosition().getAlignment()));
            }
            if (element.getPosition().getWidth() != null) {
                footer.setWidth(element.getPosition().getWidth());
            }
        }
        
        applyStyle(footer, element.getStyle(), content);  // Now has 3 parameters
        
        document.add(footer);
    }
}