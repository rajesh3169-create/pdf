
// File: processor/TableElementProcessor.java
package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableElementProcessor extends BaseElementProcessor {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        PdfTemplate.TableContent tableContent;
        
        if (element.getContent() instanceof Map) {
            tableContent = objectMapper.convertValue(element.getContent(), PdfTemplate.TableContent.class);
        } else {
            throw new IllegalArgumentException("Table content must be a valid TableContent object");
        }
        
        if (element.getProperties() != null && element.getProperties().containsKey("dataSource")) {
            String dataSource = element.getProperties().get("dataSource").toString();
            Object tableData = data.get(dataSource);
            
            if (tableData instanceof List) {
                tableContent = buildTableFromDataSource((List<?>) tableData, tableContent);
            }
        }
        
        int numColumns = tableContent.getHeaders() != null ? tableContent.getHeaders().size() : 
                        (tableContent.getRows() != null && !tableContent.getRows().isEmpty() ? 
                         tableContent.getRows().get(0).size() : 1);
        
        Table table;
        
        if (tableContent.getTableStyle() != null && tableContent.getTableStyle().getColumnWidths() != null) {
            Float[] widths = tableContent.getTableStyle().getColumnWidths();
            float[] columnWidths = new float[widths.length];
            for (int i = 0; i < widths.length; i++) {
                columnWidths[i] = widths[i];
            }
            table = new Table(UnitValue.createPercentArray(columnWidths));
        } else {
            table = new Table(numColumns);
        }
        
        if (element.getPosition() != null && element.getPosition().getWidth() != null) {
            table.setWidth(UnitValue.createPointValue(element.getPosition().getWidth()));
        } else {
            table.setWidth(UnitValue.createPercentValue(100));
        }
        
        if (tableContent.getHeaders() != null) {
            for (String header : tableContent.getHeaders()) {
                String resolvedHeader = resolveContent(header, data);
                Cell cell = new Cell().add(new Paragraph(resolvedHeader));
                
                if (tableContent.getTableStyle() != null && tableContent.getTableStyle().getHeaderStyle() != null) {
                    applyCellStyle(cell, tableContent.getTableStyle().getHeaderStyle());
                }
                
                table.addHeaderCell(cell);
            }
        }
        
        if (tableContent.getRows() != null) {
            boolean alternate = false;
            for (List<String> row : tableContent.getRows()) {
                for (String cellValue : row) {
                    String resolvedValue = resolveContent(cellValue, data);
                    Cell cell = new Cell().add(new Paragraph(resolvedValue));
                    
                    if (tableContent.getTableStyle() != null) {
                        PdfTemplate.Style cellStyle = alternate && tableContent.getTableStyle().getAlternateRowStyle() != null
                            ? tableContent.getTableStyle().getAlternateRowStyle()
                            : tableContent.getTableStyle().getRowStyle();
                        
                        if (cellStyle != null) {
                            applyCellStyle(cell, cellStyle);
                        }
                    }
                    
                    table.addCell(cell);
                }
                alternate = !alternate;
            }
        }
        
        document.add(table);
    }

    private PdfTemplate.TableContent buildTableFromDataSource(List<?> dataList, 
                                                               PdfTemplate.TableContent templateContent) {
        if (dataList.isEmpty()) {
            return templateContent;
        }
        
        List<List<String>> rows = new ArrayList<>();
        
        for (Object item : dataList) {
            if (item instanceof Map) {
                Map<?, ?> rowMap = (Map<?, ?>) item;
                List<String> row = new ArrayList<>();
                
                if (templateContent.getHeaders() != null) {
                    for (String header : templateContent.getHeaders()) {
                        Object value = rowMap.get(header);
                        row.add(value != null ? value.toString() : "");
                    }
                } else {
                    for (Object value : rowMap.values()) {
                        row.add(value != null ? value.toString() : "");
                    }
                }
                
                rows.add(row);
            }
        }
        
        templateContent.setRows(rows);
        return templateContent;
    }
} 