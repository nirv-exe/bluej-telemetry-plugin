package com.uwi.comp3607;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Base Telemetry Class for each event to extend
/**
 * Base class for telemetry events. Stores each event's type and creation time.
 * Subclasses provide report text and can override methods that identify event
 * types for report calculations or provide extra JSON details.
 */
public abstract class TelemetryEvent {
    /**
     * Local date and time captured when this event is constructed.
     */
    private final LocalDateTime timestamp;
    /**
     * Category label used to identify this event in telemetry reports.
     */
    private final String eventType;

    /**
     * Initializes the event category and captures the current local date and time.
     *
     * @param eventType the category label to store
     */
    public TelemetryEvent(String eventType){
        this.eventType = eventType;
        timestamp = LocalDateTime.now();
    }

    /**
     * Returns the local timestamp captured at construction.
     *
     * @return the event creation timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the category label stored for this event.
     *
     * @return the event category
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Produces the human-readable event description used in exported reports.
     *
     * @return a textual description of the event
     */
    public abstract String output();

    /**
     * Indicates whether this event represents a successful compilation attempt.
     * The base implementation returns {@code false}; relevant subclasses override it.
     *
     * @return {@code false} for the base implementation
     */
    public boolean isSuccessfulCompilation() {
        return false;
    }

    /**
     * Indicates whether this event represents an overall failed compilation attempt.
     * The base implementation returns {@code false}; relevant subclasses override it.
     *
     * @return {@code false} for the base implementation
     */
    public boolean isFailedCompilationAttempt() {
        return false;
    }

    /**
     * Indicates whether this event represents an individual compilation error.
     * The base implementation returns {@code false}; relevant subclasses override it.
     *
     * @return {@code false} for the base implementation
     */
    public boolean isCompilationError() {
        return false;
    }

    /**
     * Indicates whether this event represents a class creation.
     * The base implementation returns {@code false}; relevant subclasses override it.
     *
     * @return {@code false} for the base implementation
     */
    public boolean isClassCreation() {
        return false;
    }

    /**
     * Provides structured properties to merge into the JSON event object.
     * Subclasses may supply event-specific properties.
     *
     * @return a new, mutable empty map in the base implementation
     */
    public Map<String, Object> getAdditionalDetails() {
        return new HashMap<String, Object>();
    }
}
