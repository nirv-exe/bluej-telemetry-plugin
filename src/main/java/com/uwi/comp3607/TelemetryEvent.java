package com.uwi.comp3607;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    public abstract String output();

    public boolean isSuccessfulCompilation() {
        return false;
    }

    public boolean isFailedCompilationAttempt() {
        return false;
    }

    public boolean isCompilationError() {
        return false;
    }

    public Map<String, Object> getAdditionalDetails() {
        return new HashMap<String, Object>();
    }
}
