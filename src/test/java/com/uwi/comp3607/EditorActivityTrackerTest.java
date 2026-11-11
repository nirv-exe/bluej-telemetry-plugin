
package com.uwi.comp3607;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class EditorActivityTrackerTest {

    @Test
    void shouldStartWithZeroEdits() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        assertEquals(0, tracker.getEditCount());
    }

    @Test
    void shouldCountRecordedEdits() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        tracker.recordEdit(new EditorInteractionEvent("INSERT"), new EditorInteractionEvent("INSERT").getTimestamp());
        tracker.recordEdit(new EditorInteractionEvent("INSERT"), new EditorInteractionEvent("INSERT").getTimestamp());
        tracker.recordEdit(new EditorInteractionEvent("DELETE"), new EditorInteractionEvent("DELETE").getTimestamp());

        assertEquals(3, tracker.getEditCount());
    }

    @Test
    void shouldCalculateAverageEditInterval() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        LocalDateTime start = LocalDateTime.of(2026, 11, 11, 10, 0, 0);

        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start);
        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start.plusSeconds(5));
        tracker.recordEdit(new EditorInteractionEvent("DELETE"), start.plusSeconds(65));

        assertEquals(32.5, tracker.getAverageEditIntervalSeconds(), 0.001);
    }

    @Test
    void shouldCalculateIdleTime() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        LocalDateTime start = LocalDateTime.of(2026, 11, 11, 10, 0, 0);

        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start);
        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start.plusSeconds(5));
        tracker.recordEdit(new EditorInteractionEvent("DELETE"), start.plusSeconds(65));

        assertEquals(60, tracker.getIdleTimeSeconds());
    }
}
