package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.util.Map;

public class TextElementProcessor extends BaseElementProcessor {
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String content = resolveContent(element.getContent(), data);
        
        Text text = new Text(content);
        Paragraph paragraph = new Paragraph(text);
        
        if (element.getPosition() != null && element.getPosition().getAlignment() != null) {
            paragraph.setTextAlignment(getTextAlignment(element.getPosition().getAlignment()));
        }
        
        if (element.getPosition() != null && element.getPosition().getWidth() != null) {
            paragraph.setWidth(element.getPosition().getWidth());
        }
        
        applyStyle(paragraph, element.getStyle(), content);  // Now has 3 parameters
        
        document.add(paragraph);
    }
}

