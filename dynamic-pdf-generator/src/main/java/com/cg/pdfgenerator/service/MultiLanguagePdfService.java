package com.cg.pdfgenerator.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MultiLanguagePdfService {
    
    private final Map<String, String> fontPaths = new HashMap<>();
    private final Map<String, PdfFont> fontCache = new HashMap<>();
    
    public MultiLanguagePdfService() {
        initializeFonts();
    }
    
    private void initializeFonts() {
        // Standard fonts (built-in iText fonts)
        fontPaths.put("ENGLISH", "Helvetica");
        fontPaths.put("LATIN", "Helvetica");
        
        // Unicode fonts - You need to add these font files to resources/fonts/
        // Download free fonts from Google Fonts or other sources
        fontPaths.put("CHINESE", "fonts/NotoSansSC-Regular.ttf");
        fontPaths.put("JAPANESE", "fonts/NotoSansJP-Regular.ttf");
        fontPaths.put("KOREAN", "fonts/NotoSansKR-Regular.ttf");
        fontPaths.put("ARABIC", "fonts/NotoSansArabic-Regular.ttf");
        fontPaths.put("HINDI", "fonts/NotoSansDevanagari-Regular.ttf");
        fontPaths.put("THAI", "fonts/NotoSansThai-Regular.ttf");
        fontPaths.put("HEBREW", "fonts/NotoSansHebrew-Regular.ttf");
        fontPaths.put("CYRILLIC", "fonts/NotoSans-Regular.ttf");
        fontPaths.put("VIETNAMESE", "fonts/NotoSans-Regular.ttf");
        fontPaths.put("TAMIL", "fonts/NotoSansTamil-Regular.ttf");
        
        log.info("Font paths initialized for multiple languages");
    }
    
    public PdfFont getFont(String language) throws Exception {
        if (language == null || language.isEmpty()) {
            language = "ENGLISH";
        }
        
        language = language.toUpperCase();
        
        // Check cache first
        if (fontCache.containsKey(language)) {
            return fontCache.get(language);
        }
        
        String fontPath = fontPaths.getOrDefault(language, "Helvetica");
        PdfFont font;
        
        try {
            if (fontPath.equals("Helvetica") || fontPath.equals("Times-Roman") || fontPath.equals("Courier")) {
                // Standard font
                font = PdfFontFactory.createFont(fontPath);
            } else {
                // Unicode font from file
                font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, 
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            }
            
            fontCache.put(language, font);
            log.info("Font loaded for language: {}", language);
            return font;
            
        } catch (Exception e) {
            log.error("Error loading font for language: {}. Falling back to Helvetica", language, e);
            // Fallback to standard font
            font = PdfFontFactory.createFont("Helvetica");
            return font;
        }
    }
    
    public String detectLanguage(String text) {
        if (text == null || text.isEmpty()) {
            return "ENGLISH";
        }
        
        // Chinese detection
        if (text.matches(".*[\\u4E00-\\u9FA5]+.*")) {
            return "CHINESE";
        }
        
        // Japanese detection (Hiragana, Katakana)
        if (text.matches(".*[\\u3040-\\u309F\\u30A0-\\u30FF]+.*")) {
            return "JAPANESE";
        }
        
        // Korean detection
        if (text.matches(".*[\\uAC00-\\uD7AF]+.*")) {
            return "KOREAN";
        }
        
        // Arabic detection
        if (text.matches(".*[\\u0600-\\u06FF]+.*")) {
            return "ARABIC";
        }
        
        // Hindi/Devanagari detection
        if (text.matches(".*[\\u0900-\\u097F]+.*")) {
            return "HINDI";
        }
        
        // Thai detection
        if (text.matches(".*[\\u0E00-\\u0E7F]+.*")) {
            return "THAI";
        }
        
        // Hebrew detection
        if (text.matches(".*[\\u0590-\\u05FF]+.*")) {
            return "HEBREW";
        }
        
        // Cyrillic detection
        if (text.matches(".*[\\u0400-\\u04FF]+.*")) {
            return "CYRILLIC";
        }
        
        // Tamil detection
        if (text.matches(".*[\\u0B80-\\u0BFF]+.*")) {
            return "TAMIL";
        }
        
        // Default to English/Latin
        return "ENGLISH";
    }
    
    public boolean isRTL(String language) {
        if (language == null) {
            return false;
        }
        
        String lang = language.toUpperCase();
        return lang.equals("ARABIC") || lang.equals("HEBREW") || lang.equals("PERSIAN");
    }
    
    public void registerCustomFont(String language, String fontPath) {
        fontPaths.put(language.toUpperCase(), fontPath);
        fontCache.remove(language.toUpperCase()); // Clear cache to reload
        log.info("Custom font registered for language: {}", language);
    }
}