
// File: processor/FooterElementProcessor.java
package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.Map;

public class FooterElementProcessor extends BaseElementProcessor {
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String content = resolveContent(element.getContent(), data);
        
        Paragraph footer = new Paragraph(content);
        
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
        
        applyStyle(footer, element.getStyle(), content);
        
        document.add(footer);
    }
}
