package com.uwi.comp3607;

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
    public void export(List<TelemetryEvent> events, String projectName) {
        if (events == null || events.isEmpty()) {
            return;
        }

        TelemetryReportAggregator summary = new TelemetryReportAggregator(events);
        String fileName = projectName + "_Telemetry.pdf";

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
                contentStream.newLine();
                contentStream.newLine();


                contentStream.setFont(titleFont, 11);
                contentStream.showText("Compilation Error Breakdown");
                contentStream.newLine();

                contentStream.setFont(bodyFont, 10);

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

                if (errorCounts.isEmpty()) {
                    contentStream.showText("No compilation errors recorded.");
                    contentStream.newLine();
                } else {
                    for (String errorCode : errorCounts.keySet()) {
                        contentStream.showText(errorCode + ": " + errorCounts.get(errorCode));
                        contentStream.newLine();
                    }
                }

                contentStream.newLine();

                contentStream.endText();
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

            document.save(fileName);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export PDF telemetry", e);
        }
    }
}