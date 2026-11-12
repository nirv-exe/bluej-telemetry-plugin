package com.uwi.comp3607;

public class ClassDeletionEvent extends TelemetryEvent
{
    private String name;

    public ClassDeletionEvent(String className) 
    {
        super("Class Deleted");
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
