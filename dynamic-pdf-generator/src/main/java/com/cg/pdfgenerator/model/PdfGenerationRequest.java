
// ============================================
// PdfGenerationRequest.java
// ============================================
package com.cg.pdfgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfGenerationRequest {
    private String templateId;
    private Map<String, Object> data; // Dynamic data to populate template
    private String outputFileName;
}