package com.uwi.comp3607;
import java.util.List;

public interface TelemetryExporter {
   void export(List<TelemetryEvent> events, String projectName);
}
