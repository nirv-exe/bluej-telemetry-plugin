package com.uwi.comp3607;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BPackage;
import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;
import bluej.extensions2.MenuGenerator;
import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;
import bluej.extensions2.event.InvocationFinishedListener;
import bluej.extensions2.event.PackageListener;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

/**
 * BlueJ extension entry point that connects telemetry storage, event listeners,
 * and JSON/PDF reporting. Registers class, package, compilation, execution, and
 * editor tracking, and adds a Tools menu item for the student identifier used
 * in JSON reports. Package closing triggers report export.
 */
public class TelemetryExtension extends Extension
{
    /**
     * BlueJ extension API instance retained for listener registration and removal.
     */
    private BlueJ blueJ;
    /**
     * Listener that records class rename and uncompiled-state modification events.
     */
    private ClassListener classListener;
    /**
     * Listener that maintains the package class-name baseline and records lifecycle changes.
     */
    private PackageListener projectListener;
    /**
     * Package-close listener responsible for invoking the configured report exporters.
     */
    private PackageListener exportListener;
    /**
     * Registrar that discovers Java editors and attaches document telemetry listeners.
     */
    private EditorRegistrationListener editorRegistrationListener;
    /**
     * Listener that records user-generated compilation outcomes and compiler-error events.
     */
    private CompileListener compilationListener;
    /**
     * Auxiliary listener that checks class changes and editor registrations when compilation starts.
     */
    private CompileListener pollingCompileListener;
    /**
     * Listener that converts completed BlueJ invocations into execution telemetry.
     */
    private InvocationFinishedListener executionListener;
    /**
     * Shared in-memory event store created at startup and supplied to telemetry listeners.
     */
    private TelemetryStorage storage;
    

    // When this is called, the extension begin to work
    /**
     * Creates shared storage, sets up JSON and PDF exporters, and registers the
     * listeners. Loads the saved student ID and adds a Tools menu item for updating
     * and saving a nonblank ID. A separate listener checks for class changes and
     * newly available editors when compilation starts.
     *
     * @param bluej the BlueJ extension API supplied by the host
     */
    @Override 
    public void startup(BlueJ bluej){
        this.blueJ = bluej;
        this.storage = new TelemetryStorage();

        ProjectLifecycleListener lifeCycleTracker = new ProjectLifecycleListener(storage);
        this.projectListener = lifeCycleTracker;
        editorRegistrationListener = new EditorRegistrationListener(storage);

        List<TelemetryExporter> exporters = new ArrayList<>();
        
        JsonExporter jsonExporter = new JsonExporter();
        
        jsonExporter.setStudentId(
            bluej.getExtensionPropertyString("studentId", "")
        );

        exporters.add(jsonExporter);
        exporters.add(new PdfExporter());

        // Add the Student ID option to BlueJ's Tools menu
        bluej.setMenuGenerator(new MenuGenerator() {

            /**
             * Creates a Tools menu item that opens the student identifier dialog.
             * A nonblank trimmed response updates the JSON exporter and the saved extension
             * property; cancellation or blank input leaves the identifier unchanged.
             *
             * @param pkg the package for which BlueJ requests the menu item, unused here
             * @return a new menu item for setting the student identifier
             */
            @Override
            public MenuItem getToolsMenuItem(BPackage pkg) {

                MenuItem item = new MenuItem("Set Student ID");

                item.setOnAction(e -> {

                    String currentId =
                        bluej.getExtensionPropertyString("studentId", "");

                    TextInputDialog dialog =
                        new TextInputDialog(currentId);

                    dialog.setTitle("Telemetry Plugin");
                    dialog.setHeaderText("Student Information");
                    dialog.setContentText("Enter your Student ID:");

                    dialog.showAndWait().ifPresent(input -> {

                        String id = input.trim();

                        if (!id.isEmpty()) {
                            jsonExporter.setStudentId(id);
                            bluej.setExtensionPropertyString(
                                "studentId", id
                            );
                        }
                    });
                });

                return item;
            }
        });

        this.exportListener =
            new ExportPackageListener(storage, exporters);

        
        bluej.addPackageListener(lifeCycleTracker);
        bluej.addPackageListener(editorRegistrationListener);
        bluej.addPackageListener(exportListener);
        

        this.compilationListener = new CompilationListener(storage);
        this.pollingCompileListener = new CompileListener() {
            /**
             * Polls for class additions or removals and registers newly available editors.
             * This auxiliary callback runs for every compilation-start notification without
             * checking whether the compilation was user generated.
             *
             * @param event the BlueJ compilation-start notification
             */
            @Override
            public void compileStarted(CompileEvent event) {
                lifeCycleTracker.checkForClassChanges();
                editorRegistrationListener.checkForEditors();
            }
            /**
             * Receives a successful-compilation notification without taking action.
             * This auxiliary listener polls only at compilation start; the separate
             * {@link CompilationListener} handles result recording.
             *
             * @param event the BlueJ successful-compilation notification
             */
            @Override public void compileSucceeded(CompileEvent event) {}
            /**
             * Receives a failed-compilation notification without taking action.
             * This auxiliary listener polls only at compilation start; the separate
             * {@link CompilationListener} handles result recording.
             *
             * @param event the BlueJ failed-compilation notification
             */
            @Override public void compileFailed(CompileEvent event) {}
            /**
             * Receives a compiler-warning notification without taking action.
             * This auxiliary listener polls only at compilation start; the separate
             * {@link CompilationListener} handles result recording.
             *
             * @param event the BlueJ compiler-warning notification
             */
            @Override public void compileWarning(CompileEvent event) {}
            /**
             * Receives a compiler-error notification without taking action.
             * This auxiliary listener polls only at compilation start; the separate
             * {@link CompilationListener} handles result recording.
             *
             * @param event the BlueJ compiler-error notification
             */
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

    /**
     * Reports compatibility without checking the host BlueJ version or environment.
     *
     * @return {@code true} for every compatibility check
     */
    @Override
    public boolean isCompatible(){
        return true;
    }

    /**
     * Returns the extension version displayed by BlueJ.
     *
     * @return the version string {@code 1.0}
     */
    @Override 
    public String getVersion(){
        return("1.0");
    }

    /**
     * Returns the extension name displayed by BlueJ.
     *
     * @return {@code BlueJ Telemetry Plugin}
     */
    @Override 
    public String getName(){
        return("BlueJ Telemetry Plugin");
    }

    /**
     * Unregisters the class, package, compilation, and invocation listeners from BlueJ.
     * Prints a shutdown message; this method does not itself export or clear stored events.
     */
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

    /**
     * Returns the extension description displayed by BlueJ.
     *
     * @return a description of telemetry collection and JSON/PDF reporting
     */
    @Override 
    public String getDescription(){
        return("Collects BlueJ telemetry data and exports JSON/PDF reports.");
    }

    /**
     * Indicates that the extension does not supply an information URL.
     *
     * @return {@code null}
     */
    @Override
    public URL getURL() {
        return null;
    }
}
