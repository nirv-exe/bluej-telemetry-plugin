package com.uwi.comp3607;

import java.io.File;
import java.util.List;

import bluej.extensions2.BPackage;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

/**
 * Exports a snapshot of stored telemetry when a BlueJ package closes.
 * Events are cleared only after every configured exporter returns normally;
 * a runtime export failure retains them for a later export attempt.
 */
public class ExportPackageListener implements PackageListener {
    /**
     * Shared event store whose snapshot is exported and cleared after successful completion.
     */
    private final TelemetryStorage storage;
    /**
     * Exporters called in list order. Stores the original list rather than a copy.
     */
    private final List<TelemetryExporter> exporters;

    /**
     * Creates a package-close exporter using the supplied storage and exporter list.
     *
     * @param storage the shared telemetry event store
     * @param exporters the exporters to invoke in order; {@code null} or empty disables export
     */
    public ExportPackageListener(TelemetryStorage storage, List<TelemetryExporter> exporters) {
        this.storage = storage;
        this.exporters = exporters;
    }

    /**
     * Receives package-open notifications without exporting or changing storage.
     *
     * @param event the BlueJ package-open notification
     */
    @Override
    public void packageOpened(PackageEvent event) {
    }

    /**
     * Finds the project's folder and exports a copy of the stored event list.
     * Skips export if project lookup fails, there are no events, or no exporters
     * are configured. Runs exporters in list order and clears storage only after
     * all finish successfully. If an exporter throws a runtime exception, prints
     * the error and keeps the events, even if earlier exporters wrote reports.
     *
     * @param event the BlueJ package-close notification used to find the project
     */
    @Override
    public void packageClosing(PackageEvent event) {
        String projectName = "BlueJProject";
        File projectDirectory;
        try {
            BPackage pkg = event.getPackage();

            projectDirectory = pkg.getProject().getDir();
            projectName = projectDirectory.getName();

        } catch (Exception e) {
            System.out.println(
                "Could not locate BlueJ project folder: "
                + e.getMessage()
            );
            return;
        }

        List<TelemetryEvent> sessionEvents = storage.getEvents();

        if (sessionEvents.isEmpty() || exporters == null || exporters.isEmpty()) {
            return;
        }

        try {
            for (TelemetryExporter exporter : exporters) {
                exporter.export(sessionEvents, projectName, projectDirectory);
            }

            // Clear only after every exporter succeeds
            storage.clearEvents();

        } catch (RuntimeException e) {
            System.err.println(
                "Telemetry export failed: " + e.getMessage()
            );
        }
    }
}