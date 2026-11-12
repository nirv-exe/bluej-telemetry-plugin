package com.uwi.comp3607;

public class ClassDeletionEvent extends TelemetryEvent
{
    private String name;

    public ClassDeletionEvent(String className, String studentID) 
    {
        super("Class Deleted", studentID);
        this.name = className;
    }

    public String getName()
    {
        return name;
    }

    public String output() 
    {
        return "Class (" + this.getName() + ") deleted.";
    }
}
