
package com.uwi.comp3607;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EditorActivityTracker {

    private final List<EditorInteractionEvent> events;
    private final List<LocalDateTime> editTimes;

    private static final long IDLE_THRESHOLD = 30;

    public EditorActivityTracker() {

        events = new ArrayList<>();
        editTimes = new ArrayList<>();
    }

    public void recordEdit(EditorInteractionEvent event) {

        if(event == null){
            throw new IllegalArgumentException("Event cannot be null");
        }

        recordEdit(event, event.getTimestamp());
    }

    // Allows us to supply specific times in JUnit tests
    public void recordEdit(EditorInteractionEvent event, LocalDateTime time) {

        if (event == null || time == null) {
            throw new IllegalArgumentException("Event and time cannot be null");
        }

        if (!editTimes.isEmpty() && time.isBefore(editTimes.get(editTimes.size() - 1))) {
            throw new IllegalArgumentException("Edit times must be chronological");
        }

        events.add(event);
        editTimes.add(time);
    }

    public int getEditCount() {

        return events.size();
    }

    // Duration between the first and last recorded edit
    public long getEditingDurationSeconds() {

        if (editTimes.size() < 2) {
            return 0;
        }

        LocalDateTime start = editTimes.get(0);
        LocalDateTime end = editTimes.get(editTimes.size() - 1);

        return Duration.between(start, end).getSeconds();
    }

    // Average seconds between editing events
    public double getAverageEditIntervalSeconds() {

        if (editTimes.size() < 2) {
            return 0.0;
        }

        long totalMilliseconds = 0;

        for (int i = 1; i < editTimes.size(); i++) {
            totalMilliseconds += Duration.between(editTimes.get(i - 1), editTimes.get(i)).toMillis();
        }

        return totalMilliseconds / 1000.0 / (editTimes.size() - 1);
    }

    // Total time in gaps longer than the idle threshold
    public long getIdleTimeSeconds() {

        long totalIdleSeconds = 0;

        for (int i = 1; i < editTimes.size(); i++) {
            long gap = Duration.between(editTimes.get(i - 1), editTimes.get(i)).getSeconds();

            if (gap > IDLE_THRESHOLD) {
                totalIdleSeconds += gap;
            }
        }

        return totalIdleSeconds;
    }

    
    // Average number of edits per minute
    public double getEditFrequencyPerMinute() {

        if (editTimes.size() < 2) {
            return 0.0;
        }

        long totalMilliseconds = Duration.between(
                editTimes.get(0),
                editTimes.get(editTimes.size() - 1)).toMillis();

        if (totalMilliseconds == 0) {
            return 0.0;
        }

        double totalMinutes = totalMilliseconds / 60000.0;

        return events.size() / totalMinutes;
    }

    
}
