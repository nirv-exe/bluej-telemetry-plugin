package com.uwi.comp3607;

import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;

/**
 * Records class modifications when BlueJ reports a rename or a change to an
 * uncompiled state. Class removals are tracked separately by
 * {@link ProjectLifecycleListener}.
 */
public class ClassDataListener implements ClassListener 
{
    /**
     * Shared destination for class modification events.
     */
    private final TelemetryStorage storage;

    /**
     * Creates a listener that records modifications in the supplied storage.
     *
     * @param storage the shared telemetry event store
     */
    public ClassDataListener(TelemetryStorage storage)
    {
        this.storage = storage;
    }

    /**
     * Records a modification using the class name reported after a rename.
     * The previous name is not retained.
     *
     * @param event the BlueJ class rename notification
     */
    @Override 
    public void classNameChanged(ClassEvent event)
    {
        String newName = event.getBClass().getName();
        ClassModificationEvent modEvent = new ClassModificationEvent(newName);
        storage.addEvent(modEvent);
    }

    /**
     * Receives a removal notification without recording an event.
     * Removal detection is handled by {@link ProjectLifecycleListener}.
     *
     * @param event the BlueJ class removal notification
     */
    @Override 
    public void classRemoved(ClassEvent event){
        //Handled by ProjectLifecycleListener
    }

    /**
     * Records a modification and prints a message when the class is not compiled.
     * Notifications for compiled classes do not create events.
     *
     * @param event the BlueJ class-state notification
     */
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
