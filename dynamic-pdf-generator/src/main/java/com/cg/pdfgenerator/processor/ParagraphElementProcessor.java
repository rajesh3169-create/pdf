
// File: processor/ParagraphElementProcessor.java
package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.Map;

public class ParagraphElementProcessor extends BaseElementProcessor {
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String content = resolveContent(element.getContent(), data);
        
        Paragraph paragraph = new Paragraph(content);
        
        if (element.getPosition() != null) {
            if (element.getPosition().getAlignment() != null) {
                paragraph.setTextAlignment(getTextAlignment(element.getPosition().getAlignment()));
            }
            if (element.getPosition().getWidth() != null) {
                paragraph.setWidth(element.getPosition().getWidth());
            }
        }
        
        if (element.getStyle() != null && element.getStyle().getLineHeight() != null) {
            paragraph.setMultipliedLeading(element.getStyle().getLineHeight());
        }
        
        applyStyle(paragraph, element.getStyle(), content);
        
        document.add(paragraph);
    }
}