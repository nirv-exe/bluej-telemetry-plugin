package com.uwi.comp3607;

import java.io.IOException;
import java.io.UncheckedIOException;
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
                contentStream.showText("Total Events Recorded: " + summary.getTotalEvents()
                        + " | Total Session Duration: " + summary.getTotalSessionSeconds() + "s");
                contentStream.newLine();
                contentStream.showText("Active Coding Time: " + summary.getActiveTimeSeconds()
                        + "s | Idle Time: " + summary.getIdleTimeSeconds() + "s");
                contentStream.newLine();
                contentStream.showText(String.format(
                        "Compilation Attempts: %d | Success Rate: %.1f%% | Syntax Errors Logged: %d",
                        summary.getTotalCompilations(), summary.getCompileSuccessRate(), summary.getCompilationErrors()));
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


                contentStream.setFont(titleFont, 11);
                contentStream.showText("Event Timeline & Class Progression");
                contentStream.newLine();
                contentStream.setFont(bodyFont, 10);

                int lineCount = 0;
                int maxLines = 20;

                for (TelemetryEvent event : events) {

                    // Create another page when the current one is full
                    if (lineCount >= maxLines) {

                        contentStream.endText();
                        contentStream.close();

                        page = new PDPage();
                        document.addPage(page);

                        contentStream =
                            new PDPageContentStream(document, page);

                        contentStream.beginText();
                        contentStream.setFont(bodyFont, 10);
                        contentStream.setLeading(14.5f);
                        contentStream.newLineAtOffset(50, 750);

                        lineCount = 0;
                        maxLines = 40;
                    }

                    String eventType = event.getEventType();

                    if (eventType.equals("LIFECYCLE") ||
                            eventType.equals("Class Modified")) {
                        eventType = "CLASS";
                    }

                    String rawLine =
                        "[" + event.getTimestamp() + "] "
                        + "[" + eventType + "] "
                        + event.output();

                    String cleanLine = rawLine.replace("\n", " ")
                        .replace("\r", " ");

                    if (cleanLine.length() > 95) {
                        cleanLine = cleanLine.substring(0, 92) + "...";
                    }

                    contentStream.showText(cleanLine);
                    contentStream.newLine();

                    lineCount++;
                }



                contentStream.endText();
            } finally {
                contentStream.close();
            }

            document.save(fileName);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export PDF telemetry", e);
        }
    }
}