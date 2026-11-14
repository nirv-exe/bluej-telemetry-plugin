package com.uwi.comp3607;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class PdfExporter implements TelemetryExporter {

    @Override
    public void export(List<TelemetryEvent> events, String projectName, File outputDirectory) {
        if (events == null || events.isEmpty()) {
            return;
        }

        TelemetryReportAggregator summary = new TelemetryReportAggregator(events);
        DateTimeFormatter filenameFormatter =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

        String sessionTimestamp = events.get(0).getTimestamp().format(filenameFormatter);

        String fileName = projectName + "_" + sessionTimestamp + "_Telemetry.pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                contentStream.beginText();
                contentStream.setFont(titleFont, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Telemetry Report: " + projectName);

                contentStream.setFont(titleFont, 11);
                contentStream.setLeading(14.5f);
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Session Summary Metrics");
                contentStream.newLine();

                contentStream.setFont(bodyFont, 10);
                contentStream.showText("Total Events Recorded: " + summary.getTotalEvents() + " | Total Session Duration: " + summary.getTotalSessionSeconds()+ "s");
                contentStream.newLine();
                contentStream.showText("Active Coding Time: " + summary.getActiveTimeSeconds() + "s | Idle Time: " + summary.getIdleTimeSeconds() + "s");
                contentStream.newLine();
                contentStream.showText(String.format("Compilation Attempts: %d | Success Rate: %.1f%% | Compilation Errors Logged: %d", summary.getTotalCompilations(), summary.getCompileSuccessRate(), summary.getCompilationErrors()));
                contentStream.endText();

                // Active vs. Idle Time Chart
                contentStream.beginText();
                contentStream.setFont(titleFont, 11);
                contentStream.newLineAtOffset(50, 630);
                contentStream.showText("Estimated Coding Activity");
                contentStream.endText();

                long activeSeconds = summary.getActiveTimeSeconds();
                long idleSeconds = summary.getIdleTimeSeconds();

                // Use the longest time as the scale
                long longestTime = Math.max(1, Math.max(activeSeconds, idleSeconds));

                float activeWidth = 300f * activeSeconds / longestTime;
                float idleWidth = 300f * idleSeconds / longestTime;

                // Active coding label
                contentStream.beginText();
                contentStream.setFont(bodyFont, 10);
                contentStream.newLineAtOffset(50, 605);
                contentStream.showText("Active Coding: " + activeSeconds + "s");
                contentStream.endText();

                // Blue bar for active coding
                contentStream.setNonStrokingColor(new Color(60, 120, 200));
                contentStream.addRect(50, 585, activeWidth, 12);
                contentStream.fill();

                // Idle time label
                contentStream.beginText();
                contentStream.setFont(bodyFont, 10);
                contentStream.newLineAtOffset(50, 555);
                contentStream.showText("Idle Time: " + idleSeconds + "s");
                contentStream.endText();

                // Gray bar for idle time
                contentStream.setNonStrokingColor(new Color(180, 180, 180));
                contentStream.addRect(50, 535, idleWidth, 12);
                contentStream.fill();

                // Reset drawing color to black
                contentStream.setNonStrokingColor(Color.BLACK);

                // Continue with compilation errors
                contentStream.beginText();
                contentStream.setFont(titleFont, 11);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 490);
                contentStream.showText("Compilation Error Breakdown");
                contentStream.endText();

                Map<String, Integer> errorCounts = new HashMap<>();

                for (TelemetryEvent event : events) {

                    if (event.isCompilationError()) {

                        Object code = event.getAdditionalDetails().get("errorCode");

                        String errorCode = (code == null) ? "UNKNOWN" : code.toString();

                        if (errorCounts.containsKey(errorCode)) {
                            errorCounts.put(errorCode, errorCounts.get(errorCode) + 1);
                        } else {
                            errorCounts.put(errorCode, 1);
                        }
                    }
                }

                // Table dimensions
                float tableX = 50;
                float tableTop = 465;
                float tableWidth = 500;
                float columnX = 405;
                float rowHeight = 22;

                // Limit displayed categories to fit on page 1
                int rows = Math.min(8, Math.max(1, errorCounts.size()));
                float tableHeight = (rows + 1) * rowHeight;

                // Draw table border
                contentStream.setStrokingColor(Color.BLACK);
                contentStream.addRect(
                    tableX, tableTop - tableHeight,
                    tableWidth, tableHeight
                );

                // Draw vertical column divider
                contentStream.moveTo(columnX, tableTop);
                contentStream.lineTo(columnX, tableTop - tableHeight);

                // Draw horizontal row dividers
                for (int i = 1; i <= rows; i++) {
                    float y = tableTop - i * rowHeight;

                    contentStream.moveTo(tableX, y);
                    contentStream.lineTo(tableX + tableWidth, y);
                }

                contentStream.stroke();

                // Table headings
                contentStream.beginText();
                contentStream.setFont(titleFont, 10);
                contentStream.newLineAtOffset(58, tableTop - 15);
                contentStream.showText("Error Category");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(titleFont, 10);
                contentStream.newLineAtOffset(columnX + 10, tableTop - 15);
                contentStream.showText("Frequency");
                contentStream.endText();

                // Table data
                int rowNumber = 0;

                if (errorCounts.isEmpty()) {

                    contentStream.beginText();
                    contentStream.setFont(bodyFont, 10);
                    contentStream.newLineAtOffset(58, tableTop - 37);
                    contentStream.showText("No compilation errors recorded.");
                    contentStream.endText();

                } else {

                    for (Map.Entry<String, Integer> entry
                            : errorCounts.entrySet()) {

                        if (rowNumber >= 8) {
                            break;
                        }

                        float y = tableTop - (rowNumber + 1)
                            * rowHeight - 15;

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, 10);
                        contentStream.newLineAtOffset(58, y);
                        contentStream.showText(entry.getKey());
                        contentStream.endText();

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, 10);
                        contentStream.newLineAtOffset(columnX + 10, y);
                        contentStream.showText(entry.getValue().toString());
                        contentStream.endText();

                        rowNumber++;
                    }
                }

                // Explain if some categories could not fit
                if (errorCounts.size() > 8) {

                    contentStream.beginText();
                    contentStream.setFont(bodyFont, 9);
                    contentStream.newLineAtOffset(
                        50, tableTop - tableHeight - 20
                    );
                    contentStream.showText(
                        "Additional error categories are available in the JSON report."
                    );
                    contentStream.endText();
                }
                contentStream.close();

                // Create the second and final page
                page = new PDPage();
                document.addPage(page);

                contentStream = new PDPageContentStream(document, page);

                // Draw timeline heading
                contentStream.beginText();
                contentStream.setFont(titleFont, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Class Creation Timeline");
                contentStream.endText();

                // Draw vertical timeline line
                contentStream.setLineWidth(2);
                contentStream.moveTo(75, 700);
                contentStream.lineTo(75, 130);
                contentStream.stroke();

                int yPosition = 680;
                int displayedEvents = 0;

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                for (TelemetryEvent event : events) {

                    if (event.isClassCreation() && displayedEvents < 12) {

                        // Draw a small marker on the timeline
                        contentStream.addRect(71, yPosition - 4, 8, 8);
                        contentStream.fill();

                        // Display the timestamp and event
                        String description = event.output();

                        if (description.length() > 55) {
                            description = description.substring(0, 52) + "...";
                        }

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, 10);
                        contentStream.newLineAtOffset(95, yPosition);
                        contentStream.showText(event.getTimestamp().format(formatter) + " - " + description);
                        contentStream.endText();

                        yPosition -= 45;
                        displayedEvents++;
                    }
                }

                if (displayedEvents == 0) {
                    contentStream.beginText();
                    contentStream.setFont(bodyFont, 10);
                    contentStream.newLineAtOffset(95, 680);
                    contentStream.showText("No class creation events recorded.");
                    contentStream.endText();
                }
            } finally {
                contentStream.close();
            }

            File outputFile = new File(outputDirectory, fileName);

            document.save(outputFile);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export PDF telemetry", e);
        }
    }
}