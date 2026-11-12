package com.uwi.comp3607;

import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.ClassEvent;

public class ClassDataListener implements ClassListener 
{
    public void classNameChanged(ClassEvent event)
    {
        String name = event.getOldName();
        String newName = event.getBClass().getName();
        System.out.println("Class Renamed.\nOld Name:\t" + name + "\nNew Name:\t" + newName);
    }
    public void classRemoved(ClassEvent event)
    {
        String name = event.getBClass().getName();
        System.out.println("Class '" + name + "' has been deleted.");
    }
    public void classStateChanged(ClassEvent event)
    {
        String name = event.getBClass().getName();
        if(event.isClassCompiled()){
            System.out.println("Class (" + name + ") has been compiled.");
        }else{
            System.out.println("Class (" + name + ") has been modified.");
        }
    }
}
