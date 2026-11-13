package com.uwi.comp3607;

import java.util.ArrayList;
import java.util.List;

public class TelemetryStorage {
    private final List<TelemetryEvent> eventList;

    public TelemetryStorage() {
        this.eventList = new ArrayList<>();
    }

    public void addEvent(TelemetryEvent event){
        this.eventList.add(event);
    }

    public List<TelemetryEvent> getEventAndClear() {
        List<TelemetryEvent> snapshot = new ArrayList<>(this.eventList);

        this.eventList.clear();
        return snapshot;
    }

    // Returns a copy without clearing stored events
    public List<TelemetryEvent> getEvents() {
        return new ArrayList<>(eventList);
    }

    // Clears the stored events
    public void clearEvents() {
        eventList.clear();
    }

}
