package com.uwi.comp3607;

import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.ClassEvent;

public class ClassDataListener implements ClassListener 
{
    private final TelemetryStorage storage;

    public ClassDataListener(TelemetryStorage storage)
    {
        this.storage = storage;
    }

    public void classNameChanged(ClassEvent event)
    {
        String newName = event.getBClass().getName();
        ClassModificationEvent modEvent = new ClassModificationEvent(newName);
        storage.addEvent(modEvent);
    }
    public void classRemoved(ClassEvent event){}
    public void classStateChanged(ClassEvent event)
    {
        String name = event.getBClass().getName();
        ClassModificationEvent modEvent = new ClassModificationEvent(event.getBClass().getName());
        storage.addEvent(modEvent);
        if(event.isClassCompiled()){
            System.out.println("Class (" + name + ") has been compiled.");
        }else{
            System.out.println("Class (" + name + ") has been modified.");
        }
    }
}
