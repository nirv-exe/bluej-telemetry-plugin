package com.uwi.comp3607;

public class EditorInteractionEvent extends TelemetryEvent{


    private final String action;

    public EditorInteractionEvent(String studentID, String action) {
        super("EDITOR_INTERACTION", studentID);
        this.action = action;
    }

    public String getAction(){
        return action;
    }

}
