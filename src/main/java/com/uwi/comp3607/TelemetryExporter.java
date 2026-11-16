package com.uwi.comp3607;
import java.io.File;
import java.util.List;

/**
 * Defines report export from an event sequence to a project output directory.
 * The plugin uses JSON and PDF implementations, which leave event storage
 * management to the package-close listener.
 */
public interface TelemetryExporter {
   /**
    * Writes a telemetry report for the supplied project and event sequence.
    * The JSON and PDF implementations skip {@code null} or empty event lists and
    * name reports using the project name and first event's timestamp.
    *
    * @param events the events to report in their supplied order, or {@code null}
    * @param projectName the project label included in report content and filenames
    * @param outputDirectory the directory in which to write the report
    * @throws java.io.UncheckedIOException if report output fails due to an I/O error
    */
   void export(List<TelemetryEvent> events, String projectName, File outputDirectory);
}
