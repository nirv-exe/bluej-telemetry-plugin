package com.uwi.comp3607;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculates session, compilation, and estimated editing statistics for JSON and
 * PDF reports. Successful compilations and failed-attempt events count as
 * compilation attempts; individual compiler errors are counted separately.
 * Editing estimates use editor event times. Events are processed in the given
 * order without sorting.
 */
public class TelemetryReportAggregator {
    /**
     * Number of entries in the supplied event list, or zero for a {@code null} list.
     */
    private final int totalEvents;
    /**
     * Count of successful compilations plus overall failed compilation attempts.
     */
    private final int totalCompilations;
    /**
     * Number of events classified as successful compilations.
     */
    private final int successfulCompilations;
    /**
     * Number of individual compilation-error events, excluding failed-attempt markers.
     */
    private final int compilationErrors;
    /**
     * Successful compilation attempts as a percentage of all attempts, or zero without attempts.
     */
    private final double compileSuccessRate;
    /**
     * Nonnegative whole-second span between the first and last events in the supplied order.
     */
    private final long totalSessionSeconds;
    /**
     * Sum of whole-second gaps exceeding 30 seconds between editor-interaction events.
     */
    private final long idleTimeSeconds;
    /**
     * Time between the first and last edits, minus estimated idle time, in whole
     * seconds. Never negative.
     */
    private final long activeTimeSeconds;
    /**
     * Whole-second gaps between consecutive compilation attempts in the supplied event order.
     */
    private final List<Long> compilationIntervals;

    /**
     * Calculates report statistics once from the given events. A {@code null} or empty
     * list gives zero counts and durations. Session duration uses the first and last
     * events; editing estimates use only editor interactions. Compilation intervals
     * use overall attempts, excluding individual compiler-error events.
     *
     * @param events the event list in reporting order, containing non-null events;
     *         editor-interaction timestamps must be nondecreasing; may be {@code null}
     * @throws IllegalArgumentException if an editor-interaction timestamp precedes
     *         the previous editor-interaction timestamp
     */
    public TelemetryReportAggregator(List<TelemetryEvent> events) {
        this.totalEvents = (events == null) ? 0 : events.size();
        compilationIntervals = new ArrayList<>();
        LocalDateTime previousCompilationTime = null;
        int compSuccess = 0;
        int compFailedAttempts = 0;
        int syntaxErrors = 0;
        EditorActivityTracker tracker = new EditorActivityTracker();

        if (events != null) {
            for (TelemetryEvent event : events) {
                if (event.isSuccessfulCompilation()) {
                    compSuccess++;
                } else if (event.isFailedCompilationAttempt()) {
                    compFailedAttempts++;
                } else if (event.isCompilationError()) {
                    syntaxErrors++;
                }

                if (event.isSuccessfulCompilation() || event.isFailedCompilationAttempt()) {

                    LocalDateTime currentTime = event.getTimestamp();

                    if (previousCompilationTime != null) {

                        long interval = Duration.between(
                            previousCompilationTime,
                            currentTime
                        ).getSeconds();

                        compilationIntervals.add(interval);
                    }

                    previousCompilationTime = currentTime;
                }

                if ("EDITOR_INTERACTION".equals(event.getEventType())) {
                    tracker.recordEdit(
                        new EditorInteractionEvent("EDIT"),
                        event.getTimestamp()
                    );
                }
            }
        }

        this.successfulCompilations = compSuccess;
        this.totalCompilations = compSuccess + compFailedAttempts;
        this.compilationErrors = syntaxErrors;
        this.compileSuccessRate = (totalCompilations == 0) ? 0.0 : (compSuccess * 100.0) / totalCompilations;

        if (events != null && events.size() >= 2) {
            LocalDateTime start = events.get(0).getTimestamp();
            LocalDateTime end = events.get(events.size() - 1).getTimestamp();
            this.totalSessionSeconds = Math.max(0, Duration.between(start, end).getSeconds());
        } else {
            this.totalSessionSeconds = 0;
        }

        this.idleTimeSeconds = tracker.getIdleTimeSeconds();

        this.activeTimeSeconds = Math.max(0, tracker.getEditingDurationSeconds() - this.idleTimeSeconds);
    }

    /**
     * Returns the number of entries supplied for this report.
     *
     * @return the total event count
     */
    public int getTotalEvents() { return totalEvents; }
    /**
     * Returns the number of overall compilation attempts, excluding diagnostic-only events.
     *
     * @return the sum of successful and failed compilation attempts
     */
    public int getTotalCompilations() { return totalCompilations; }
    /**
     * Returns the number of successful compilation attempts.
     *
     * @return the successful compilation count
     */
    public int getSuccessfulCompilations() { return successfulCompilations; }
    /**
     * Returns the number of individual compilation-error events.
     * Several errors may belong to one failed attempt.
     *
     * @return the compilation-error event count
     */
    public int getCompilationErrors() { return compilationErrors; }
    /**
     * Returns the percentage of compilation attempts that succeeded.
     *
     * @return the success percentage, or zero if there were no attempts
     */
    public double getCompileSuccessRate() { return compileSuccessRate; }
    /**
     * Returns the span between the first and last supplied events, clamped to zero.
     *
     * @return the duration in whole seconds, or zero for fewer than two events
     */
    public long getTotalSessionSeconds() { return totalSessionSeconds; }
    /**
     * Returns estimated idle time from gaps between consecutive editor interactions.
     * Each gap whose whole-second duration exceeds 30 seconds contributes its full duration.
     *
     * @return the estimated idle duration in whole seconds
     */
    public long getIdleTimeSeconds() { return idleTimeSeconds; }
    /**
     * Returns the nonnegative first-to-last edit span minus estimated idle time.
     * Time before the first edit and after the last edit is not included.
     *
     * @return the estimated active editing duration in whole seconds
     */
    public long getActiveTimeSeconds() { return activeTimeSeconds; }
    /**
     * Returns a copy of the gaps between consecutive compilation attempts, measured
     * in whole seconds. Gaps follow the given event order and may be negative if
     * timestamps are out of order.
     *
     * @return a new mutable list of compilation intervals in seconds
     */
    public List<Long> getCompilationIntervals() { return new ArrayList<>(compilationIntervals); }
}