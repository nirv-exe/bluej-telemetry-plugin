package com.uwi.comp3607;

import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;

public class ClassDataListener implements ClassListener 
{
    private final TelemetryStorage storage;

    public ClassDataListener(TelemetryStorage storage)
    {
        this.storage = storage;
    }

    @Override 
    public void classNameChanged(ClassEvent event)
    {
        String newName = event.getBClass().getName();
        ClassModificationEvent modEvent = new ClassModificationEvent(newName);
        storage.addEvent(modEvent);
    }

    @Override 
    public void classRemoved(ClassEvent event){
        //Handled by ProjectLifecycleListener
    }

    @Override
public void classStateChanged(ClassEvent event) {
    String name = event.getBClass().getName();

    if (!event.isClassCompiled()) {
        ClassModificationEvent modEvent =
            new ClassModificationEvent(name);

        storage.addEvent(modEvent);

        System.out.println("Class (" + name + ") has been modified.");
    }
}
}
