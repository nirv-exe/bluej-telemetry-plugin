package com.uwi.comp3607;

public class ClassModificationEvent extends TelemetryEvent
{
    private final String name;

    public ClassModificationEvent(String className) 
    {
        super("Class Modified");
        this.name = className;
    }

    public String getName() 
    {
        return name;
    }

    @Override 
    public String output() 
    {
        return "Class (" + this.getName() + ") modified.";
    }
}
