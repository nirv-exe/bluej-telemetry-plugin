package com.uwi.comp3607;

public class ProjectCreationEvent extends TelemetryEvent
{
    private String name;

    public ProjectCreationEvent(String className, String studentID) 
    {
        super("Project Created", studentID);
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
