package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

public class ImageElementProcessor extends BaseElementProcessor {

    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        String imagePath = resolveContent(element.getContent(), data);

        if (imagePath == null || imagePath.isBlank()) {
            System.err.println("[WARN] No image path provided for element: " + element.getId());
            return;
        }

        Image image = null;
        try {
            if (imagePath.startsWith("data:image")) {
                // Base64 image (inline)
                String base64Data = imagePath.substring(imagePath.indexOf(",") + 1);
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                image = new Image(ImageDataFactory.create(imageBytes));

            } else if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                // Remote image (load with custom User-Agent to avoid HTTP 403)
                ImageData imageData = loadRemoteImageWithUserAgent(imagePath);
                image = new Image(imageData);

            } else {
                // Local file path
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    System.err.println("[WARN] Image file not found: " + imagePath);
                    return;
                }
                image = new Image(ImageDataFactory.create(imagePath));
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load image from: " + imagePath + " — " + e.getMessage());
            return; // Skip the image but continue PDF creation
        }

        // Apply position and style
        if (element.getPosition() != null) {
            if (element.getPosition().getWidth() != null) {
                image.setWidth(element.getPosition().getWidth());
            }
            if (element.getPosition().getHeight() != null) {
                image.setHeight(element.getPosition().getHeight());
            }

            if (element.getPosition().getAlignment() != null) {
                HorizontalAlignment alignment = switch (element.getPosition().getAlignment().toUpperCase()) {
                    case "CENTER" -> HorizontalAlignment.CENTER;
                    case "RIGHT" -> HorizontalAlignment.RIGHT;
                    default -> HorizontalAlignment.LEFT;
                };
                image.setHorizontalAlignment(alignment);
            }
        }

        // Optional auto-scaling
        if (element.getProperties() != null && element.getProperties().containsKey("autoScale")) {
            Object autoScaleObj = element.getProperties().get("autoScale");
            if (autoScaleObj instanceof Boolean autoScale && autoScale) {
                image.setAutoScale(true);
            }
        }

        document.add(image);
    }

    /**
     * Loads remote image with a browser-like User-Agent header to bypass 403 restrictions.
     */
    private ImageData loadRemoteImageWithUserAgent(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/120.0 Safari/537.36");
        conn.setConnectTimeout(7000);
        conn.setReadTimeout(7000);

        try (InputStream in = conn.getInputStream()) {
            byte[] imageBytes = in.readAllBytes();
            return ImageDataFactory.create(imageBytes);
        }
    }
}
