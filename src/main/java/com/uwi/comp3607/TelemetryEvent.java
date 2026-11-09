package com.uwi.comp3607;

import java.time.LocalDateTime;

// Base Telemetry Class for each even to extend
public abstract class TelemetryEvent {
    private final LocalDateTime timestamp;
    private final String eventType;
    private final String studentID;

    public TelemetryEvent(String eventType, String studentID){
        this.eventType = eventType;
        this.studentID = studentID;
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public String getStudentId() {
        return studentID;
    }
}
