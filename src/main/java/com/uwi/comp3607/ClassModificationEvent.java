package com.uwi.comp3607;

/**
 * Stores the class name reported after a rename or an uncompiled-state
 * notification. Provides a description of the modification for reports.
 */
public class ClassModificationEvent extends TelemetryEvent
{
    /**
     * Class name reported when the modification event is created.
     */
    private final String name;

    /**
     * Creates a timestamped event with the category {@code Class Modified}.
     *
     * @param className the class name associated with the notification
     */
    public ClassModificationEvent(String className) 
    {
        super("Class Modified");
        this.name = className;
    }

    /**
     * Returns the name recorded for the modified class.
     *
     * @return the stored class name
     */
    public String getName() 
    {
        return name;
    }

    /**
     * Formats a modification message containing the recorded class name.
     *
     * @return the class modification description
     */
    @Override 
    public String output() 
    {
        return "Class (" + this.getName() + ") modified.";
    }
}
