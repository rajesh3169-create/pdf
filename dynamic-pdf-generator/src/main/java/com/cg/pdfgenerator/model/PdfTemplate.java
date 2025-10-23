
// ============================= MODELS =============================
// File: model/PdfTemplate.java
package com.cg.pdfgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfTemplate {
    private String templateId;
    private String templateName;
    private PageSettings pageSettings;
    private List<Element> elements;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageSettings {
        private String size; // A4, LETTER, etc.
        private Float marginTop;
        private Float marginBottom;
        private Float marginLeft;
        private Float marginRight;
        private String orientation; // PORTRAIT, LANDSCAPE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Element {
        private String type; // TEXT, IMAGE, TABLE, NUMBER, PARAGRAPH, HEADER, FOOTER
        private String id;
        private Position position;
        private Style style;
        private Object content;
        private Map<String, Object> properties;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Position {
        private Float x;
        private Float y;
        private Float width;
        private Float height;
        private String alignment;
        private String verticalAlignment;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Style {
        private String fontFamily;
        private Float fontSize;
        private String fontColor;
        private Boolean bold;
        private Boolean italic;
        private Boolean underline;
        private String backgroundColor;
        private Float borderWidth;
        private String borderColor;
        private Float padding;
        private Float lineHeight;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableContent {
        private List<String> headers;
        private List<List<String>> rows;
        private TableStyle tableStyle;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableStyle {
        private Style headerStyle;
        private Style rowStyle;
        private Style alternateRowStyle;
        private Float[] columnWidths;
        private Boolean showBorders;
        private Float borderWidth;
    }
}