
// File: processor/HeaderElementProcessor.java
package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.Map;

public class HeaderElementProcessor extends BaseElementProcessor {
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String content = resolveContent(element.getContent(), data);
        
        Paragraph header = new Paragraph(content);
        header.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
        
        float fontSize = element.getStyle() != null && element.getStyle().getFontSize() != null 
            ? element.getStyle().getFontSize() : 18f;
        header.setFontSize(fontSize);
        
        if (element.getPosition() != null) {
            if (element.getPosition().getAlignment() != null) {
                header.setTextAlignment(getTextAlignment(element.getPosition().getAlignment()));
            }
            if (element.getPosition().getWidth() != null) {
                header.setWidth(element.getPosition().getWidth());
            }
        }
        
        applyStyle(header, element.getStyle(), content);
        
        document.add(header);
    }
}
