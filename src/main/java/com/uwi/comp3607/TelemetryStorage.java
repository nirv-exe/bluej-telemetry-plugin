package com.uwi.comp3607;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores telemetry events in memory in the order they are added. Listeners add
 * events, and exporters read copies of the list. Events are not saved automatically,
 * access is not synchronized, and callers decide when to clear storage.
 */
public class TelemetryStorage {
    /**
     * List of recorded events waiting to be read or exported.
     */
    private final List<TelemetryEvent> eventList;

    /**
     * Creates an empty in-memory telemetry event store.
     */
    public TelemetryStorage() {
        this.eventList = new ArrayList<>();
    }

    /**
     * Appends an event without validation or deduplication.
     *
     * @param event the event reference to append; {@code null} is not rejected
     */
    public void addEvent(TelemetryEvent event){
        this.eventList.add(event);
    }

    /**
     * Copies the current event sequence and then empties this store.
     * The returned list is independent, but contains the same event references.
     *
     * @return a mutable snapshot of events removed from storage
     */
    public List<TelemetryEvent> getEventAndClear() {
        List<TelemetryEvent> snapshot = new ArrayList<>(this.eventList);

        this.eventList.clear();
        return snapshot;
    }

    // Returns a copy without clearing stored events
    /**
     * Returns a copy of the event list without clearing storage. Changing the
     * returned list does not change storage, but both lists refer to the same
     * event objects.
     *
     * @return a mutable snapshot in insertion order
     */
    public List<TelemetryEvent> getEvents() {
        return new ArrayList<>(eventList);
    }

    // Clears the stored events
    /**
     * Removes all stored event references from this in-memory store.
     */
    public void clearEvents() {
        eventList.clear();
    }

}
