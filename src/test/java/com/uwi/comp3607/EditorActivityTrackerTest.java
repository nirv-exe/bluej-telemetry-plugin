
package com.uwi.comp3607;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void shouldCalculateEditFrequency() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        LocalDateTime start = LocalDateTime.of(2026, 11, 11, 10, 0, 0);

        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start);
        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start.plusSeconds(30));
        tracker.recordEdit(new EditorInteractionEvent("DELETE"), start.plusMinutes(1));
        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start.plusMinutes(2));

        assertEquals(2.0, tracker.getEditFrequencyPerMinute(), 0.001);
    }

    
    @Test
    void shouldRejectNullEvent() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        assertThrows(IllegalArgumentException.class, () -> tracker.recordEdit(null));
    }

    @Test
    void shouldRejectNullTimestamp() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        assertThrows(IllegalArgumentException.class, () -> tracker.recordEdit(new EditorInteractionEvent("INSERT"), null));
    }

    @Test
    void shouldRejectOutOfOrderTimestamps() {

        EditorActivityTracker tracker = new EditorActivityTracker();

        LocalDateTime start = LocalDateTime.of(2026, 11, 11, 10, 0, 0);

        tracker.recordEdit(new EditorInteractionEvent("INSERT"), start);

        assertThrows(IllegalArgumentException.class, () -> tracker.recordEdit(new EditorInteractionEvent("DELETE"), start.minusSeconds(5)));
    }


}
