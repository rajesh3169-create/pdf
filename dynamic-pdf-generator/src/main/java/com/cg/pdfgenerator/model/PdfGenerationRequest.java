
// File: model/PdfGenerationRequest.java
package com.cg.pdfgenerator.model;

import lombok.*;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfGenerationRequest {
    private String templateId;
    private Map<String, Object> data;
    private String outputFileName;
}

