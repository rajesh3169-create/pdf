package com.cg.pdfgenerator.processor;

import com.cg.pdfgenerator.model.PdfTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class TableElementProcessor extends BaseElementProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Document document, PdfTemplate.Element element, Map<String, Object> data) throws Exception {
        // Convert content to TableContent
        PdfTemplate.TableContent tableContent;

        if (element.getContent() instanceof Map) {
            tableContent = objectMapper.convertValue(element.getContent(), PdfTemplate.TableContent.class);
        } else {
            throw new IllegalArgumentException("Table content must be a valid TableContent object");
        }

        // Resolve dynamic data source if specified
        if (element.getProperties() != null && element.getProperties().containsKey("dataSource")) {
            String dataSource = element.getProperties().get("dataSource").toString();
            Object tableData = data.get(dataSource);

            if (tableData instanceof List) {
                tableContent = buildTableFromDataSource((List<?>) tableData, tableContent);
            }
        }

        // Determine number of columns safely
        int numColumns = 0;
        if (tableContent.getHeaders() != null && !tableContent.getHeaders().isEmpty()) {
            numColumns = tableContent.getHeaders().size();
        } else if (tableContent.getRows() != null && !tableContent.getRows().isEmpty()) {
            numColumns = tableContent.getRows().get(0).size();
        }

        // Safety fallback
        if (numColumns <= 0) {
            numColumns = 1; // Prevent iText crash
            log.warn("TableElementProcessor: No valid headers or rows found for element '{}', defaulting to 1 column.", element.getId());
        }

        // Create table
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

        // Set table width
        if (element.getPosition() != null && element.getPosition().getWidth() != null) {
            table.setWidth(UnitValue.createPointValue(element.getPosition().getWidth()));
        } else {
            table.setWidth(UnitValue.createPercentValue(100));
        }

        // Add headers
        if (tableContent.getHeaders() != null && !tableContent.getHeaders().isEmpty()) {
            for (String header : tableContent.getHeaders()) {
                String resolvedHeader = resolveContent(header, data);
                Cell cell = new Cell().add(new Paragraph(resolvedHeader));

                if (tableContent.getTableStyle() != null && tableContent.getTableStyle().getHeaderStyle() != null) {
                    applyCellStyle(cell, tableContent.getTableStyle().getHeaderStyle());
                }

                table.addHeaderCell(cell);
            }
        }

        // Add rows
        if (tableContent.getRows() != null && !tableContent.getRows().isEmpty()) {
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

        // Add the completed table to the document
        document.add(table);
    }

    /**
     * Builds table rows dynamically from a provided data source (List of Map objects)
     */
    private PdfTemplate.TableContent buildTableFromDataSource(List<?> dataList,
                                                              PdfTemplate.TableContent templateContent) {
        if (dataList.isEmpty()) {
            log.warn("TableElementProcessor: Data source is empty for dynamic table.");
            return templateContent;
        }

        List<List<String>> rows = new java.util.ArrayList<>();

        for (Object item : dataList) {
            if (item instanceof Map) {
                Map<?, ?> rowMap = (Map<?, ?>) item;
                List<String> row = new java.util.ArrayList<>();

                // Use header order to extract values
                if (templateContent.getHeaders() != null && !templateContent.getHeaders().isEmpty()) {
                    for (String header : templateContent.getHeaders()) {
                        Object value = rowMap.get(header);
                        row.add(value != null ? value.toString() : "");
                    }
                } else {
                    // If no headers, just add all values in map order
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
