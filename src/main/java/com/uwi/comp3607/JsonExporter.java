package com.uwi.comp3607;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Writes a pretty-printed JSON telemetry report containing the configured student
 * identifier, project name, aggregate metrics, and each event's description and
 * structured details. Report filenames use the first event's timestamp.
 */
public class JsonExporter implements TelemetryExporter {
    /**
     * Student identifier included in JSON reports; defaults to {@code STUDENT_PLACEHOLDER}.
     */
    private String studentId = "STUDENT_PLACEHOLDER";

    /**
     * Updates the report identifier with a trimmed, nonblank value.
     * A {@code null} or blank argument leaves the current identifier unchanged.
     *
     * @param studentId the student identifier to use for subsequent JSON reports
     */
    public void setStudentId(String studentId) {
        if (studentId != null && !studentId.isBlank()) {
            this.studentId = studentId.trim();
        }
    }
    /**
     * Writes session metrics and event objects to a timestamped JSON file.
     * Each event includes its category, timestamp, description, and any additional
     * properties supplied by the event. A {@code null} or empty list produces no file.
     * The destination directory must already exist; a matching filename is overwritten.
     *
     * @param events the events in reporting order, containing non-null entries;
     *         editor-interaction timestamps must be nondecreasing; may be {@code null}
     * @param projectName the project label for the JSON content and filename
     * @param outputDirectory the destination directory for the JSON file
     * @throws UncheckedIOException if opening, writing, or closing the report fails
     * @throws IllegalArgumentException if editor-interaction timestamps are out of order
     */
    @Override
    public void export(List<TelemetryEvent> events, String projectName, File outputDirectory) {
        if (events == null || events.isEmpty()) {
            return;
        }

        TelemetryReportAggregator summary = new TelemetryReportAggregator(events);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject root = new JsonObject();
        root.addProperty("studentId", studentId);
        root.addProperty("projectName", projectName);
        root.addProperty("totalSessionDurationSeconds", summary.getTotalSessionSeconds());
        root.addProperty("activeCodingTimeSeconds", summary.getActiveTimeSeconds());
        root.addProperty("idleTimeSeconds", summary.getIdleTimeSeconds());
        root.addProperty("totalCompilationAttempts", summary.getTotalCompilations());
        root.addProperty("compileSuccessRatePercent", summary.getCompileSuccessRate());        
        root.add("compilationIntervalsSeconds", gson.toJsonTree(summary.getCompilationIntervals()));
        
        JsonArray jsonArray = new JsonArray();
        
        for (TelemetryEvent event : events) {

            JsonObject obj = new JsonObject();

            obj.addProperty("eventType", event.getEventType());
            obj.addProperty("timestamp", event.getTimestamp().toString());
            obj.addProperty("details", event.output());

            JsonObject additionalDetails = gson.toJsonTree(
                event.getAdditionalDetails()
            ).getAsJsonObject();

            for (String key : additionalDetails.keySet()) {
                obj.add(key, additionalDetails.get(key));
            }

            jsonArray.add(obj);
        }

        root.add("events", jsonArray);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

        String sessionTimestamp = events.get(0).getTimestamp().format(formatter);

        String fileName = projectName + "_" + sessionTimestamp + "_Telemetry.json";

        File outputFile = new File(outputDirectory, fileName);

        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export JSON telemetry", e);
        }
    }
}