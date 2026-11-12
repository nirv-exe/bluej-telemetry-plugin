package com.uwi.comp3607;

import bluej.extensions2.editor.DocumentListener;

public class EditorDocumentListener implements DocumentListener {


    private final EditorActivityTracker tracker;
    private final TelemetryStorage storage;

    public EditorDocumentListener(EditorActivityTracker tracker, TelemetryStorage storage){

        this.tracker = tracker;
        this.storage = storage;

    }

    
    @Override
    public void textReplaced(int offset, String removedText, String insertedText, int linesRemoved, int linesAdded) {

        // Ignore changes where no text was added or removed
        if (removedText.isEmpty() && insertedText.isEmpty()) {
            return;
        }

        String action;

        // Work out what type of edit happened
        if (!removedText.isEmpty() && !insertedText.isEmpty()) {
            action = "REPLACE";
        }
        else if (!insertedText.isEmpty()) {
            action = "INSERT";
        }
        else {
            action = "DELETE";
        }

        // Record the edit in the tracker and shared storage
        EditorInteractionEvent event = new EditorInteractionEvent(action);

        tracker.recordEdit(event);
        storage.addEvent(event);
}

}
