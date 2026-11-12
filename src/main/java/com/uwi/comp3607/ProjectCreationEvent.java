package com.uwi.comp3607;

public class ProjectCreationEvent extends TelemetryEvent
{
    private String name;

    public ProjectCreationEvent(String className) 
    {
        super("Project Created");
        this.name = className;
    }

    public String getName() 
    {
        return name;
    }

    public String output() 
    {
        return "Project (" + this.getName() + ") created.";
    }
}
