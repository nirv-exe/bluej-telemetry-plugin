package com.uwi.comp3607;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;
import bluej.extensions2.event.InvocationFinishedListener;
import bluej.extensions2.event.PackageListener;

public class TelemetryExtension extends Extension
{
    private BlueJ blueJ;
    private ClassListener classListener;
    private PackageListener projectListener;
    private PackageListener exportListener;
    private EditorRegistrationListener editorRegistrationListener;
    private CompileListener compilationListener;
    private CompileListener pollingCompileListener;
    private InvocationFinishedListener executionListener;
    private TelemetryStorage storage;
    

    // When this is called, the extension begin to work
    @Override 
    public void startup(BlueJ bluej){
        this.blueJ = bluej;
        this.storage = new TelemetryStorage();

        ProjectLifecycleListener lifeCycleTracker = new ProjectLifecycleListener(storage);
        this.projectListener = lifeCycleTracker;
        editorRegistrationListener = new EditorRegistrationListener(storage);

        List<TelemetryExporter> exporters = new ArrayList<>();
        exporters.add(new JsonExporter());
        exporters.add(new PdfExporter());
        this.exportListener = new ExportPackageListener(storage, exporters);
        
        bluej.addPackageListener(lifeCycleTracker);
        bluej.addPackageListener(editorRegistrationListener);
        bluej.addPackageListener(exportListener);
        

        this.compilationListener = new CompilationListener(storage);
        this.pollingCompileListener = new CompileListener() {
            @Override
            public void compileStarted(CompileEvent event) {
                lifeCycleTracker.checkForClassChanges();
                editorRegistrationListener.checkForEditors();
            }
            @Override public void compileSucceeded(CompileEvent event) {}
            @Override public void compileFailed(CompileEvent event) {}
            @Override public void compileWarning(CompileEvent event) {}
            @Override public void compileError(CompileEvent event) {}
        };
        bluej.addCompileListener(compilationListener);
        bluej.addCompileListener(pollingCompileListener);

        this.executionListener = new ExecutionListener(storage);
        bluej.addInvocationFinishedListener(executionListener);

        this.classListener = new ClassDataListener(storage);
        blueJ.addClassListener(classListener);

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
        blueJ.removeClassListener(classListener);
        blueJ.removePackageListener(projectListener);
        blueJ.removePackageListener(editorRegistrationListener);
        blueJ.removePackageListener(exportListener);
        blueJ.removeCompileListener(compilationListener);
        blueJ.removeCompileListener(pollingCompileListener);
        blueJ.removeInvocationFinishedListener(executionListener);
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
