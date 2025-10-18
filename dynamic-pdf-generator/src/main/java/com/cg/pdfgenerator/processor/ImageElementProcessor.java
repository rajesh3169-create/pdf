
package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.File;
import java.util.Base64;
import java.util.Map;

public class ImageElementProcessor extends BaseElementProcessor {
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String imagePath = resolveContent(element.getContent(), data);
        
        Image image;
        
        // Check if it's a base64 encoded image
        if (imagePath.startsWith("data:image")) {
            String base64Data = imagePath.substring(imagePath.indexOf(",") + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            image = new Image(ImageDataFactory.create(imageBytes));
        } else if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            // URL image
            image = new Image(ImageDataFactory.create(imagePath));
        } else {
            // File path
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("Image file not found: " + imagePath);
            }
            image = new Image(ImageDataFactory.create(imagePath));
        }
        
        // Apply dimensions
        if (element.getPosition() != null) {
            if (element.getPosition().getWidth() != null) {
                image.setWidth(element.getPosition().getWidth());
            }
            if (element.getPosition().getHeight() != null) {
                image.setHeight(element.getPosition().getHeight());
            }
            
            // Set alignment
            if (element.getPosition().getAlignment() != null) {
                HorizontalAlignment alignment = switch (element.getPosition().getAlignment().toUpperCase()) {
                    case "CENTER" -> HorizontalAlignment.CENTER;
                    case "RIGHT" -> HorizontalAlignment.RIGHT;
                    default -> HorizontalAlignment.LEFT;
                };
                image.setHorizontalAlignment(alignment);
            }
        }
        
        // Apply auto-scaling if properties contain it
        if (element.getProperties() != null && element.getProperties().containsKey("autoScale")) {
            Boolean autoScale = (Boolean) element.getProperties().get("autoScale");
            if (autoScale) {
                image.setAutoScale(true);
            }
        }
        
        document.add(image);
    }
}