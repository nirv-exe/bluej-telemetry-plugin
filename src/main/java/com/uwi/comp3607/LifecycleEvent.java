package com.uwi.comp3607;

/**
 * Records a class creation or deletion detected by package class-name polling.
 * Creation events are also identified for the PDF report's class creation timeline.
 */
public class LifecycleEvent extends TelemetryEvent {
    /**
     * Lifecycle action label identifying class creation or deletion.
     */
    private final String action; // E.g., "CLASS_CREATED", "CLASS_DELETED"
    /**
     * Name of the class associated with the lifecycle action.
     */
    private final String className;

    // Private constructor used by the static factory methods
    /**
     * Initializes a timestamped event with category {@code LIFECYCLE}.
     *
     * @param action the lifecycle action label
     * @param className the class name associated with the action
     */
    private LifecycleEvent(String action, String className) {
        super("LIFECYCLE");
        this.action = action;
        this.className = className;
    }

    // Factory method for a created class
    /**
     * Creates an event marking a detected class creation.
     *
     * @param className the newly detected class name
     * @return a new event with action {@code CLASS_CREATED}
     */
    public static LifecycleEvent classCreated(String className) {
        return new LifecycleEvent("CLASS_CREATED", className);
    }

    // Factory method for a deleted class
    /**
     * Creates an event marking a detected class deletion.
     *
     * @param className the class name no longer present
     * @return a new event with action {@code CLASS_DELETED}
     */
    public static LifecycleEvent classDeleted(String className) {
        return new LifecycleEvent("CLASS_DELETED", className);
    }

    /**
     * Returns the action recorded for this lifecycle change.
     *
     * @return the lifecycle action label
     */
    public String getAction() { 
        return action; 
    }
    
    /**
     * Returns the class name recorded for this lifecycle change.
     *
     * @return the stored class name
     */
    public String getClassName() { 
        return className; 
    }

    /**
     * Formats the lifecycle action and class name for reporting.
     *
     * @return the lifecycle change description
     */
    @Override
    public String output() {
        return "Lifecycle action: " + action + " on class " + className;
    }

    /**
     * Identifies creation events for report filtering.
     *
     * @return {@code true} if the action is {@code CLASS_CREATED}
     */
    @Override
    public boolean isClassCreation() {
        return action.equals("CLASS_CREATED");
    }
}
