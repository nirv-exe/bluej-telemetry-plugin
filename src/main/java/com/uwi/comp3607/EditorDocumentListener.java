package com.uwi.comp3607;

import bluej.extensions2.editor.DocumentListener;

/**
 * Identifies text changes as insertions, deletions, or replacements. Records each
 * change in the activity tracker and shared telemetry storage, unless both text
 * strings are empty. The events do not store the changed text, its position,
 * or line counts.
 */
public class EditorDocumentListener implements DocumentListener {


    /**
     * Activity tracker that receives each classified edit and its timestamp.
     */
    private final EditorActivityTracker tracker;
    /**
     * Shared event store used to include editor interactions in exported reports.
     */
    private final TelemetryStorage storage;

    /**
     * Connects document change notifications to activity tracking and event storage.
     *
     * @param tracker the activity tracker for this editor
     * @param storage the shared telemetry event store
     */
    public EditorDocumentListener(EditorActivityTracker tracker, TelemetryStorage storage){

        this.tracker = tracker;
        this.storage = storage;

    }

    
    /**
     * Records one interaction based on whether text was removed, inserted, or both.
     * Changes with empty removed and inserted strings are ignored. Other callback
     * details are accepted but are not used for classification or stored in the event.
     *
     * @param offset the reported document offset, unused by this listener
     * @param removedText the removed text, used to detect deletion or replacement
     * @param insertedText the inserted text, used to detect insertion or replacement
     * @param linesRemoved the reported removed-line count, unused by this listener
     * @param linesAdded the reported added-line count, unused by this listener
     */
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
