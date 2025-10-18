package com.cg.pdfgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.data.mongodb.core.mapping.Document;

/**
 * PDF Element model for template elements
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfElement {
    
    private String id;
    private String type; // paragraph, table, image, spacer, header, footer
    private String content;
    private Float x;
    private Float y;
    private Float width;
    private Float height;
    private Float fontSize;
    private Boolean bold;
    private Boolean italic;
    private String fontColor;
    private String backgroundColor;
    private String alignment; // LEFT, CENTER, RIGHT, JUSTIFIED
    private String borderStyle; // SOLID, DASHED, DOTTED, NONE
    private Float borderWidth;
    private String borderColor;
    private Float paddingTop;
    private Float paddingBottom;
    private Float paddingLeft;
    private Float paddingRight;
    private String fontName; // Arial, Helvetica, Times, Courier, etc.
    private Boolean underline;
    private String textDecoration;
    private Integer lineHeight;
    private String pageBreak; // BEFORE, AFTER, BOTH
    private Boolean repeatable; // Repeat on each page
    
    /**
     * Validate element
     */
    public void validate() {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Element type is required");
        }
        
        // Validate type is one of the supported types
        String typeUpper = type.toUpperCase();
        if (!typeUpper.matches("PARAGRAPH|TABLE|IMAGE|SPACER|HEADER|FOOTER|LINE|RECTANGLE")) {
            throw new IllegalArgumentException("Invalid element type: " + type);
        }
    }
    
    /**
     * Get element dimension string
     */
    public String getDimensions() {
        return String.format("x=%.1f, y=%.1f, width=%.1f, height=%.1f", x, y, width, height);
    }
    
    /**
     * Get element styling string
     */
    public String getStyling() {
        StringBuilder sb = new StringBuilder();
        if (fontSize != null) sb.append("fontSize=").append(fontSize).append(", ");
        if (bold != null && bold) sb.append("bold, ");
        if (italic != null && italic) sb.append("italic, ");
        if (fontColor != null) sb.append("color=").append(fontColor).append(", ");
        if (alignment != null) sb.append("align=").append(alignment);
        return sb.toString();
    }
}