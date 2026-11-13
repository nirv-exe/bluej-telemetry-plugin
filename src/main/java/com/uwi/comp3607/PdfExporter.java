package com.uwi.comp3607;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class PdfExporter implements TelemetryExporter {

    @Override
    public void export(List<TelemetryEvent> events, String projectName) {
        if (events == null || events.isEmpty()) {
            return;
        }

        String fileName = projectName + "_Telemetry.pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(titleFont, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Telemetry Report: " + projectName);

                contentStream.setFont(bodyFont, 10);
                contentStream.setLeading(14.5f);
                contentStream.newLine();
                contentStream.newLine();

                contentStream.showText("Total Events Recorded: " + events.size());
                contentStream.newLine();
                contentStream.newLine();

                int lineCount = 0;
                for (TelemetryEvent event : events) {
                    if (lineCount >= 40) {
                        break; // Prevents writing off the bottom of a single page
                    }
                    String rawLine = "[" + event.getEventType() + "] " + event.output();
                    // Sanitize newlines and long strings for PDF rendering
                    String cleanLine = rawLine.replace("\n", " ").replace("\r", " ");
                    if (cleanLine.length() > 95) {
                        cleanLine = cleanLine.substring(0, 92) + "...";
                    }
                    contentStream.showText(cleanLine);
                    contentStream.newLine();
                    lineCount++;
                }

                contentStream.endText();
            }

            document.save(fileName);
        } catch (IOException e) {
            System.err.println("Failed to export PDF telemetry: " + e.getMessage());
        }
    }
}