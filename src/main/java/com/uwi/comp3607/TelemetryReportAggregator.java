package com.uwi.comp3607;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TelemetryReportAggregator {
    private final int totalEvents;
    private final int totalCompilations;
    private final int successfulCompilations;
    private final int compilationErrors;
    private final double compileSuccessRate;
    private final long totalSessionSeconds;
    private final long idleTimeSeconds;
    private final long activeTimeSeconds;
    private final List<Long> compilationIntervals;

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

    public int getTotalEvents() { return totalEvents; }
    public int getTotalCompilations() { return totalCompilations; }
    public int getSuccessfulCompilations() { return successfulCompilations; }
    public int getCompilationErrors() { return compilationErrors; }
    public double getCompileSuccessRate() { return compileSuccessRate; }
    public long getTotalSessionSeconds() { return totalSessionSeconds; }
    public long getIdleTimeSeconds() { return idleTimeSeconds; }
    public long getActiveTimeSeconds() { return activeTimeSeconds; }
    public List<Long> getCompilationIntervals() { return new ArrayList<>(compilationIntervals); }
}