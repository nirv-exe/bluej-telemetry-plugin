package com.uwi.comp3607;
import java.net.URL;

import bluej.extensions2.BlueJ; // BlueJ API Libraries
import bluej.extensions2.Extension;

public class TelemetryExtension extends Extension{
    private BlueJ blueJ;
    private TelemetryStorage storage; 

    // When this is called, the extension begin to work
    @Override 
    public void startup(BlueJ bluej){
        this.blueJ = bluej;
        this.storage = new TelemetryStorage();

        bluej.addCompileListener(new CompilationListener(storage));
        bluej.addInvocationFinishedListener(new ExecutionListener(storage));

        // TODO: Instantiate and register event listeners here
        // e.g., bluej.addClassListener(new ProjectLifecycleListener());

        System.out.println("BlueJ Telemetry Plugin successfully started");
    }

    @Override
    public boolean isCompatible(){
        return true;
    }

    @Override 
    public String getVersion(){
        return("1.0");
    }

    @Override 
    public String getName(){
        return("BlueJ Telemetry Plugin");
    }

    @Override 
    public void terminate(){
        System.out.println("BlueJ Telemetry Plugin Stopped");
    }

    @Override 
    public String getDescription(){
        return("Collects BlueJ telemetry data and exports JSON/PDF reports.");
    }

    @Override
    public URL getURL() {
        return null;
    }
}
