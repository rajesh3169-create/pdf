package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.text.DecimalFormat;
import java.util.Map;

public class NumberElementProcessor extends BaseElementProcessor {
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String content = resolveContent(element.getContent(), data);
        
        String formattedContent = content;
        if (element.getProperties() != null && element.getProperties().containsKey("format")) {
            try {
                String format = element.getProperties().get("format").toString();
                double number = Double.parseDouble(content);
                DecimalFormat df = new DecimalFormat(format);
                formattedContent = df.format(number);
            } catch (Exception e) {
                // Use original if formatting fails
            }
        }
        
        if (element.getProperties() != null) {
            String prefix = (String) element.getProperties().get("prefix");
            String suffix = (String) element.getProperties().get("suffix");
            
            if (prefix != null) {
                formattedContent = prefix + formattedContent;
            }
            if (suffix != null) {
                formattedContent = formattedContent + suffix;
            }
        }
        
        Paragraph paragraph = new Paragraph(formattedContent);
        
        if (element.getPosition() != null) {
            if (element.getPosition().getAlignment() != null) {
                paragraph.setTextAlignment(getTextAlignment(element.getPosition().getAlignment()));
            }
            if (element.getPosition().getWidth() != null) {
                paragraph.setWidth(element.getPosition().getWidth());
            }
        }
        
        applyStyle(paragraph, element.getStyle(), formattedContent);
        
        document.add(paragraph);
    }
}