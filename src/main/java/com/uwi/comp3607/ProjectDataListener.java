package com.uwi.comp3607;

import bluej.extensions2.event.PackageListener;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.ExtensionException;

public class ProjectDataListener implements PackageListener
{
    public void packageOpened(PackageEvent event)
    {
        try{
            String name = event.getPackage().getProject().getName();
            System.out.println("Project (" + name + ") has been opened.");
        }catch(ExtensionException e){
            System.out.println("Project information not found.");
        }
    }
    public void packageClosing(PackageEvent event)
    {
        try{
            String name = event.getPackage().getProject().getName();
            System.out.println("Project (" + name + ") is closing.");
        }catch(ExtensionException e){
            System.out.println("Project information not found.");
        }
    }
}
