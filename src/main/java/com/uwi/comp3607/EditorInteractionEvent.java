package com.uwi.comp3607;

public class EditorInteractionEvent extends TelemetryEvent{
    private final String action;

    public EditorInteractionEvent(String action) {
        super("EDITOR_INTERACTION");
        this.action = action;
    }

    public String getAction(){
        return action;
    }

    @Override
    public String output() {
        return "Editor action: " + action;
    }
}
