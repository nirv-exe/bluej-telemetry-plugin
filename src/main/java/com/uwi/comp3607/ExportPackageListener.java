package com.uwi.comp3607;

import java.io.File;
import java.util.List;

import bluej.extensions2.BPackage;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

public class ExportPackageListener implements PackageListener {
    private final TelemetryStorage storage;
    private final List<TelemetryExporter> exporters;

    public ExportPackageListener(TelemetryStorage storage, List<TelemetryExporter> exporters) {
        this.storage = storage;
        this.exporters = exporters;
    }

    @Override
    public void packageOpened(PackageEvent event) {
    }

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