package com.uwi.comp3607;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonExporter implements TelemetryExporter {
    @Override
    public void export(List<TelemetryEvent> events, String projectName) {
        if (events == null || events.isEmpty()) {
            return;
        }

        TelemetryReportAggregator summary = new TelemetryReportAggregator(events);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject root = new JsonObject();
        root.addProperty("studentId", "STUDENT_PLACEHOLDER");
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


        String fileName = projectName + "_Telemetry.json";

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to export JSON telemetry", e);
        }
    }
}