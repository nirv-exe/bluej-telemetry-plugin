package com.uwi.comp3607;

/**
 * Records an editor action label as a timestamped telemetry event.
 * The document listener uses insertion, deletion, and replacement labels; this
 * event stores the label without retaining the edited text or its location.
 */
public class EditorInteractionEvent extends TelemetryEvent{
    /**
     * Action label supplied for the editor interaction.
     */
    private final String action;

    /**
     * Creates an event with category {@code EDITOR_INTERACTION} and the supplied action.
     *
     * @param action the editor action label to store
     */
    public EditorInteractionEvent(String action) {
        super("EDITOR_INTERACTION");
        this.action = action;
    }

    /**
     * Returns the action label recorded for this interaction.
     *
     * @return the stored editor action
     */
    public String getAction(){
        return action;
    }

    /**
     * Formats the stored action for a telemetry report.
     *
     * @return the editor action description
     */
    @Override
    public String output() {
        return "Editor action: " + action;
    }
}
