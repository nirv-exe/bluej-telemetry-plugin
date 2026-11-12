package com.uwi.comp3607;

public class ClassModificationEvent extends TelemetryEvent
{
    private String name;

    public ClassModificationEvent(String className) 
    {
        super("Class Modified");
        this.name = className;
    }

    public String getName() 
    {
        return name;
    }

    public String output() 
    {
        return "Class (" + this.getName() + ") modified.";
    }
}
