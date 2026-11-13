package com.uwi.comp3607;

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
        try {
            BPackage pkg = event.getPackage();
            if (pkg != null && pkg.getDir() != null) {
                projectName = pkg.getDir().getName();
            }
        } catch (Exception e) {
            System.out.println("Could not resolve project name.");
        }

        List<TelemetryEvent> sessionEvents = storage.getEventAndClear();
        if (!sessionEvents.isEmpty() && exporters != null) {
            for (TelemetryExporter exporter : exporters) {
                exporter.export(sessionEvents, projectName);
            }
        }
    }
}