package com.uwi.comp3607;

import java.time.LocalDateTime;

// Base Telemetry Class for each even to extend
public abstract class TelemetryEvent {
    private final LocalDateTime timestamp;
    private final String eventType;

    public TelemetryEvent(String eventType){
        this.eventType = eventType;
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEventType() {
        return eventType;
    }
}
