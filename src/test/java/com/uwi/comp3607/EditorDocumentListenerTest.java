
package com.uwi.comp3607;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

// Tests how the listener handles different types of editor changes
public class EditorDocumentListenerTest {

    // Checks that inserting text counts as an edit
    @Test
    void shouldRecordInsertedText() {

        EditorActivityTracker tracker = new EditorActivityTracker();
        TelemetryStorage storage = new TelemetryStorage();

        EditorDocumentListener listener = new EditorDocumentListener(tracker, storage);

        listener.textReplaced(0, "", "hello", 0, 0);

        assertEquals(1, tracker.getEditCount());
    }

    // Checks that deleting text counts as an edit
    @Test
    void shouldRecordDeletedText() {

        EditorActivityTracker tracker = new EditorActivityTracker();
        TelemetryStorage storage = new TelemetryStorage();

        EditorDocumentListener listener = new EditorDocumentListener(tracker, storage);

        listener.textReplaced(0, "hello", "", 0, 0);

        assertEquals(1, tracker.getEditCount());
    }

    // Checks that replacing existing text counts as an edit
    @Test
    void shouldRecordReplacedText() {

        EditorActivityTracker tracker = new EditorActivityTracker();
        TelemetryStorage storage = new TelemetryStorage();

        EditorDocumentListener listener = new EditorDocumentListener(tracker, storage);

        listener.textReplaced(0, "hello", "world", 0, 0);

        assertEquals(1, tracker.getEditCount());
    }

    // Makes sure an empty text change is not counted
    @Test
    void shouldIgnoreEmptyChanges() {

        EditorActivityTracker tracker = new EditorActivityTracker();
        TelemetryStorage storage = new TelemetryStorage();

        EditorDocumentListener listener = new EditorDocumentListener(tracker, storage);

        listener.textReplaced(0, "", "", 0, 0);

        assertEquals(0, tracker.getEditCount());
    }

    @Test
    void shouldRecordCorrectEditAction() {
        EditorActivityTracker tracker = new EditorActivityTracker();
        TelemetryStorage storage = new TelemetryStorage();

        EditorDocumentListener listener =
            new EditorDocumentListener(tracker, storage);

        listener.textReplaced(0, "", "hello", 0, 0);

        TelemetryEvent event =
            storage.getEventAndClear().get(0);

        assertEquals("Editor action: INSERT", event.output());
    }
}
