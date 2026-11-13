package com.uwi.comp3607;

import java.io.FileWriter;
import java.io.IOException;
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

        JsonArray jsonArray = new JsonArray();
        for (TelemetryEvent event : events) {
            JsonObject obj = new JsonObject();
            obj.addProperty("eventType", event.getEventType());
            obj.addProperty("timestamp", event.getTimestamp().toString());
            obj.addProperty("details", event.output());
            jsonArray.add(obj);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fileName = projectName + "_Telemetry.json";

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            System.err.println("Failed to export JSON telemetry: " + e.getMessage());
        }
    }
}