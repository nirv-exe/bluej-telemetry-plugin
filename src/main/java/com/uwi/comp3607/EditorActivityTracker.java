
package com.uwi.comp3607;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Records edits in time order and uses the gaps between them to estimate editing
 * activity. If a gap's duration in whole seconds exceeds 30, the entire gap counts
 * as idle time. This does not measure continuous keyboard activity.
 */
public class EditorActivityTracker {

    /**
     * Recorded edit events in insertion order, used to count editing interactions.
     */
    private final List<EditorInteractionEvent> events;
    /**
     * Chronological timestamps paired with the recorded edit events.
     */
    private final List<LocalDateTime> editTimes;

    /**
     * A gap must exceed this number of whole seconds for its full duration to count
     * as idle time.
     */
    private static final long IDLE_THRESHOLD = 30;

    /**
     * Creates an empty tracker with no recorded events or timestamps.
     */
    public EditorActivityTracker() {

        events = new ArrayList<>();
        editTimes = new ArrayList<>();
    }

    /**
     * Records an edit using the timestamp stored in the event.
     *
     * @param event the editor interaction to record
     * @throws IllegalArgumentException if the event is {@code null} or its timestamp
     *         precedes the last recorded edit time
     */
    public void recordEdit(EditorInteractionEvent event) {

        if(event == null){
            throw new IllegalArgumentException("Event cannot be null");
        }

        recordEdit(event, event.getTimestamp());
    }

    // Allows us to supply specific times in JUnit tests
    /**
     * Records an edit using the given time for activity calculations.
     * Consecutive edits may have the same time. The event itself is not changed.
     *
     * @param event the editor interaction to record
     * @param time the time to use for activity calculations
     * @throws IllegalArgumentException if either argument is {@code null},
     *         or if the time is earlier than the last recorded edit
     */
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

    /**
     * Counts the edit events accumulated by this tracker.
     *
     * @return the number of recorded edits
     */
    public int getEditCount() {

        return events.size();
    }

    // Duration between the first and last recorded edit
    /**
     * Measures the span from the first recorded edit to the last, including idle gaps.
     *
     * @return the elapsed whole seconds, or zero if fewer than two edits were recorded
     */
    public long getEditingDurationSeconds() {

        if (editTimes.size() < 2) {
            return 0;
        }

        LocalDateTime start = editTimes.get(0);
        LocalDateTime end = editTimes.get(editTimes.size() - 1);

        return Duration.between(start, end).getSeconds();
    }

    // Average seconds between editing events
    /**
     * Computes the mean gap between consecutive edits using millisecond durations.
     *
     * @return the average interval in seconds, or zero if fewer than two edits were recorded
     */
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
    /**
     * Sums entire gaps whose whole-second duration is greater than the idle threshold.
     * Time before the first edit and after the last edit is not included.
     *
     * @return the estimated idle time in whole seconds
     */
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
    /**
     * Divides the recorded edit count by the first-to-last edit span in minutes.
     * The span includes idle gaps and is measured with millisecond precision.
     *
     * @return the edits per minute, or zero for fewer than two edits or a zero-millisecond span
     */
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
