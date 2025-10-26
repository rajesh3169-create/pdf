package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.Map;

public class TextElementProcessor extends BaseElementProcessor {
    
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
        
        applyStyle(paragraph, element.getStyle(), content);
        
        document.add(paragraph);
    }
} 