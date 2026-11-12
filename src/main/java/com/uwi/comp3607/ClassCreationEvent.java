package com.uwi.comp3607;

public class ClassCreationEvent extends TelemetryEvent 
{
    private final String name;

    public ClassCreationEvent(String name)
    {
        super("Class Created");
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
    public String output()
    {
        return("Class (" + this.getName() + ") created.");
    }
}
