package com.uwi.comp3607;

public class LifecycleEvent extends TelemetryEvent {
    private final String action; // E.g., "CLASS_CREATED", "CLASS_DELETED"
    private final String className;

    // Private constructor enforcing the Factory Method pattern
    private LifecycleEvent(String action, String className) {
        super("LIFECYCLE");
        this.action = action;
        this.className = className;
    }

    // Factory method for a created class
    public static LifecycleEvent classCreated(String className) {
        return new LifecycleEvent("CLASS_CREATED", className);
    }

    // Factory method for a deleted class
    public static LifecycleEvent classDeleted(String className) {
        return new LifecycleEvent("CLASS_DELETED", className);
    }

    public String getAction() { 
        return action; 
    }
    
    public String getClassName() { 
        return className; 
    }

    @Override
    public String output() {
        return "Lifecycle action: " + action + " on class " + className;
    }

    @Override
    public boolean isClassCreation() {
        return action.equals("CLASS_CREATED");
    }
}
