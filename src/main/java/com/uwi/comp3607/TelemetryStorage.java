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

}
