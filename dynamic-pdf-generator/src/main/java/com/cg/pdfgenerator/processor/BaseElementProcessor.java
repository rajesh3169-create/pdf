// ============================= BASE PROCESSOR =============================
// File: processor/BaseElementProcessor.java
package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.util.Map;

public abstract class BaseElementProcessor implements ElementProcessor {
    
    protected String resolveContent(Object content, Map<String, Object> data) {
        if (content == null) {
            return "";
        }
        
        String contentStr = content.toString();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (contentStr.contains(placeholder)) {
                contentStr = contentStr.replace(placeholder, 
                    entry.getValue() != null ? entry.getValue().toString() : "");
            }
        }
        
        return contentStr;
    }
    
    protected void applyStyle(Paragraph paragraph, PdfTemplate.Style style, String content) throws Exception {
        if (style == null) {
            return;
        }
        
        PdfFont font = getStyledFont(style);
        if (font != null) {
            paragraph.setFont(font);
        }
        
        if (style.getFontSize() != null) {
            paragraph.setFontSize(style.getFontSize());
        }
        
        if (style.getFontColor() != null) {
            Color color = parseColor(style.getFontColor());
            if (color != null) {
                paragraph.setFontColor(color);
            }
        }
        
        if (style.getBackgroundColor() != null) {
            Color bgColor = parseColor(style.getBackgroundColor());
            if (bgColor != null) {
                paragraph.setBackgroundColor(bgColor);
            }
        }
        
        if (style.getUnderline() != null && style.getUnderline()) {
            paragraph.setUnderline();
        }
        
        if (style.getBorderWidth() != null && style.getBorderWidth() > 0) {
            Color borderColor = style.getBorderColor() != null 
                ? parseColor(style.getBorderColor()) 
                : new DeviceRgb(0, 0, 0);
            paragraph.setBorder(new SolidBorder(borderColor, style.getBorderWidth()));
        }
        
        if (style.getPadding() != null) {
            paragraph.setPadding(style.getPadding());
        }
    }
    
    protected void applyCellStyle(Cell cell, PdfTemplate.Style style) throws Exception {
        if (style == null) {
            return;
        }
        
        PdfFont font = getStyledFont(style);
        if (font != null) {
            cell.setFont(font);
        }
        
        if (style.getFontSize() != null) {
            cell.setFontSize(style.getFontSize());
        }
        
        if (style.getFontColor() != null) {
            Color color = parseColor(style.getFontColor());
            if (color != null) {
                cell.setFontColor(color);
            }
        }
        
        if (style.getBackgroundColor() != null) {
            Color bgColor = parseColor(style.getBackgroundColor());
            if (bgColor != null) {
                cell.setBackgroundColor(bgColor);
            }
        }
        
        if (style.getBorderWidth() != null && style.getBorderWidth() > 0) {
            Color borderColor = style.getBorderColor() != null 
                ? parseColor(style.getBorderColor()) 
                : new DeviceRgb(0, 0, 0);
            cell.setBorder(new SolidBorder(borderColor, style.getBorderWidth()));
        }
        
        if (style.getPadding() != null) {
            cell.setPadding(style.getPadding());
        }
    }
    
    protected PdfFont getStyledFont(PdfTemplate.Style style) throws Exception {
        if (style == null) {
            return null;
        }
        
        boolean bold = style.getBold() != null && style.getBold();
        boolean italic = style.getItalic() != null && style.getItalic();
        
        if (bold && italic) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
        } else if (bold) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } else if (italic) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        }
        
        return null;
    }
    
    protected TextAlignment getTextAlignment(String alignment) {
        if (alignment == null) {
            return TextAlignment.LEFT;
        }
        
        return switch (alignment.toUpperCase()) {
            case "CENTER" -> TextAlignment.CENTER;
            case "RIGHT" -> TextAlignment.RIGHT;
            case "JUSTIFY" -> TextAlignment.JUSTIFIED;
            default -> TextAlignment.LEFT;
        };
    }
    
    protected VerticalAlignment getVerticalAlignment(String alignment) {
        if (alignment == null) {
            return VerticalAlignment.TOP;
        }
        
        return switch (alignment.toUpperCase()) {
            case "MIDDLE" -> VerticalAlignment.MIDDLE;
            case "BOTTOM" -> VerticalAlignment.BOTTOM;
            default -> VerticalAlignment.TOP;
        };
    }
    
    protected Color parseColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            return null;
        }
        
        try {
            if (colorStr.startsWith("#")) {
                colorStr = colorStr.substring(1);
            }
            
            int r = Integer.parseInt(colorStr.substring(0, 2), 16);
            int g = Integer.parseInt(colorStr.substring(2, 4), 16);
            int b = Integer.parseInt(colorStr.substring(4, 6), 16);
            
            return new DeviceRgb(r, g, b);
        } catch (Exception e) {
            return null;
        }
    }
}