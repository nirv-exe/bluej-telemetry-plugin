package com.uwi.comp3607;
import java.net.URL;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import bluej.extensions2.BlueJ; // BlueJ API Libraries
import bluej.extensions2.Extension;
import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;
import bluej.extensions2.event.PackageListener;


public class TelemetryExtension extends Extension
{
    private BlueJ blueJ;
    private ClassListener classListener;
    private PackageListener projectListener;
    private TelemetryStorage storage;
    private EditorActivityTracker editorTracker;
    private EditorDocumentListener editorListener;

    private final Set<JavaEditor> trackedEditors = Collections.newSetFromMap(new IdentityHashMap<JavaEditor, Boolean>());

    // When this is called, the extension begin to work
    @Override 
    public void startup(BlueJ bluej){
        this.blueJ = bluej;
        this.storage = new TelemetryStorage();

        // Editor interaction tracking
        editorTracker = new EditorActivityTracker();
        editorListener = new EditorDocumentListener(editorTracker, storage);

        ProjectLifecycleListener lifeCycleTracker = new ProjectLifecycleListener(storage);
        this.projectListener = lifeCycleTracker;

        bluej.addPackageListener(lifeCycleTracker);
        bluej.addCompileListener(new CompilationListener(storage));
        bluej.addCompileListener(new CompileListener() {
            @Override
            public void compileStarted(CompileEvent event) {
                lifeCycleTracker.checkForClassChanges();
            }
            // Ignore the rest for this specific trigger
            @Override public void compileSucceeded(CompileEvent event) {}
            @Override public void compileFailed(CompileEvent event) {}
            @Override public void compileWarning(CompileEvent event) {}
            @Override public void compileError(CompileEvent event) {}
        });
        bluej.addInvocationFinishedListener(new ExecutionListener(storage));

        classListener = new ClassDataListener(storage);
        blueJ.addClassListener(classListener);

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
        blueJ.removeClassListener(classListener);
        blueJ.removePackageListener(projectListener);
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
