// ============================= PROCESSOR INTERFACE =============================
// File: processor/ElementProcessor.java
package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.itextpdf.layout.Document;
import java.util.Map;

public interface ElementProcessor {
    void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception;
}
