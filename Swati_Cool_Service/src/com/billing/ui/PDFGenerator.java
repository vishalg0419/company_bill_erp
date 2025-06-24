package com.billing.ui;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class PDFGenerator {

    public static void generateBillPDF(String billNo, String customerName, String customerAddress,
                                       String billDate, int totalAmount, TableModel tableModel) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream content = null;
        
        try {
            content = new PDPageContentStream(document, page);

            float margin = 50;
            float y = 800;
            float lineHeight = 20;
            float startX = margin;

            PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
            PDType1Font fontNormal = PDType1Font.HELVETICA;

            // COMPANY HEADER
            content.setFont(fontBold, 16);
            drawCenteredText(content, "SWATI COOL SERVICES", y);

            y -= 20;
            content.setFont(fontNormal, 12);
            drawCenteredText(content, "WINDOWS & SPLIT AIRCONDITIONERS MAINTENANCE & SERVICES", y);

            y -= 15;
            drawCenteredText(content, "Pandit Lal Tiwari Road, Durga Chawl, Sanjay Nagar, Kandivali(W), Mumbai-400 067.", y);

            y -= 20;
            drawLine(content, margin, y, 550, y);

            // CUSTOMER DETAILS
            y -= 25;
            content.setFont(fontNormal, 11);
            drawText(content, "Bill NO.: " + billNo, startX, y);
            drawText(content, "Dated: " + billDate, 430, y);

            y -= lineHeight;
            drawText(content, "MR./MRS.: " + customerName, startX, y);

            y -= lineHeight;
            drawText(content, "CUSTOMER ADDRESS:", startX, y);

            y -= lineHeight;
            drawText(content, customerAddress, startX + 20, y);

            // BILL TITLE
            y -= 30;
            content.setFont(fontBold, 14);
            drawCenteredText(content, "BILL", y);

            // TABLE HEADER
            y -= 15;
            float tableStartY = y;
            float tableWidth = 500;
            float[] colWidths = {50, 220, 70, 60, 100};
            float rowHeight = 20;

            float currentY = tableStartY;

            // Draw outer border and columns (1st page only)
            float tableHeight = rowHeight * (tableModel.getRowCount() + 2);
            drawOuterTableBorder(content, startX, currentY, tableWidth, tableHeight);
            drawColumnLines(content, startX, currentY, tableHeight, colWidths);

            content.setFont(fontBold, 11);
            drawRow(content, currentY, startX,
                    new String[]{"SR.NO.", "PARTICULARS", "RATE", "QTY", "AMOUNT"},
                    colWidths);

            content.setFont(fontNormal, 11);
            currentY -= rowHeight;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (currentY < 100) {
                    content.close();

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    currentY = 750;

                    // Re-draw table header
                    content.setFont(fontBold, 11);
                    drawRow(content, currentY, startX,
                            new String[]{"SR.NO.", "PARTICULARS", "RATE", "QTY", "AMOUNT"},
                            colWidths);

                    currentY -= rowHeight;
                    drawColumnLines(content, startX, currentY + rowHeight, rowHeight * (tableModel.getRowCount() - i + 2), colWidths);
                }

                String sr = tableModel.getValueAt(i, 0).toString();
                String part = tableModel.getValueAt(i, 1).toString();
                String rate = tableModel.getValueAt(i, 2).toString();
                String qty = tableModel.getValueAt(i, 3).toString();
                String amt = tableModel.getValueAt(i, 4).toString();

                drawRow(content, currentY, startX,
                        new String[]{sr, part, rate, qty, amt},
                        colWidths);
                currentY -= rowHeight;
            }

            // Total Row
            content.setFont(fontBold, 11);
            drawRow(content, currentY, startX,
                    new String[]{"", "TOTAL", "", "", "Rs. " + totalAmount},
                    colWidths);

            currentY -= 40;
            content.setFont(fontNormal, 11);
            drawText(content, "Receiver's Signature: ____________________________", startX, currentY);
            currentY -= 20;
            drawText(content, "Prepared by: RAM SAHAY GUPTA", startX, currentY);

            content.close();

            String filePath = "C:/Users/Vishal/Desktop/Bill_" + billNo + ".pdf";
            document.save(filePath);
            System.out.println("PDF generated successfully at: " + filePath);
            Desktop.getDesktop().open(new File(filePath));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void drawText(PDPageContentStream content, String text, float x, float y) throws IOException {
        content.beginText();
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private static void drawCenteredText(PDPageContentStream content, String text, float y) throws IOException {
        float textWidth = PDType1Font.HELVETICA.getStringWidth(text) / 1000 * 12;
        float x = (PDRectangle.A4.getWidth() - textWidth) / 2;
        drawText(content, text, x, y);
    }

    private static void drawRow(PDPageContentStream content, float y, float xStart, String[] columns, float[] widths) throws IOException {
        float x = xStart;
        for (int i = 0; i < columns.length; i++) {
            drawText(content, columns[i], x + 2, y - 12);
            x += widths[i];
        }
    }

    private static void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2) throws IOException {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private static void drawOuterTableBorder(PDPageContentStream cs, float x, float y, float width, float height) throws IOException {
        cs.setLineWidth(1.2f);
        cs.addRect(x, y - height, width, height);
        cs.stroke();
    }

    private static void drawColumnLines(PDPageContentStream cs, float x, float y, float height, float[] widths) throws IOException {
        // Draw vertical lines
        cs.setLineWidth(0.5f);
        float xPos = x;
        for (float w : widths) {
            cs.moveTo(xPos, y);
            cs.lineTo(xPos, y - height);
            xPos += w;
        }
        cs.moveTo(xPos, y);
        cs.lineTo(xPos, y - height);
        cs.stroke();

        // Draw horizontal lines
        int totalRows = (int) (height / 20);
        for (int i = 1; i <= totalRows; i++) {
            float yLine = y - (i * 20);
            if (i == 1 || i == totalRows - 1) {
                cs.setLineWidth(1.2f); // Bold line below header and above total
            } else {
                cs.setLineWidth(0.5f); // Normal inner lines
            }
            cs.moveTo(x, yLine);
            cs.lineTo(x + 500, yLine);
            cs.stroke();
        }
    }
}
