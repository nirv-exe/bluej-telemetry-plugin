package com.uwi.comp3607;

public class ProjectDeletionEvent extends TelemetryEvent
{
    private String name;

    public ProjectDeletionEvent(String className) 
    {
        super("Project Deleted");
        this.name = className;
    }

    public String getName()
    {
        return name;
    }

    public String output() 
    {
        return "Project (" + this.getName() + ") deleted.";
    }
}
