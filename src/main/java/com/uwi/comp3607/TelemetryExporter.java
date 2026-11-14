package com.uwi.comp3607;
import java.io.File;
import java.util.List;

public interface TelemetryExporter {
   void export(List<TelemetryEvent> events, String projectName, File outputDirectory);
}
