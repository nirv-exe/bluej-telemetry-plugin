package com.uwi.comp3607;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bluej.extensions2.BPackage;
import bluej.extensions2.event.PackageEvent;

public class TelemetryExporterTest {

    private static final String TEST_PROJECT = "TestProject";
    private final File jsonFile = new File(TEST_PROJECT + "_Telemetry.json");
    private final File pdfFile = new File(TEST_PROJECT + "_Telemetry.pdf");

    @AfterEach
    void tearDown() {
        if (jsonFile.exists()) jsonFile.delete();
        if (pdfFile.exists()) pdfFile.delete();
    }

    @Test
    void testJsonExporterCreatesValidFile() throws Exception {
        JsonExporter exporter = new JsonExporter();
        List<TelemetryEvent> events = List.of(
            CompilationEvent.successfulCompilation(),
            LifecycleEvent.classCreated("Student")
        );

        exporter.export(events, TEST_PROJECT);

        assertTrue(jsonFile.exists(), "JSON file should be created");
        String content = Files.readString(jsonFile.toPath());
        assertTrue(content.contains("COMPILATION"));
        assertTrue(content.contains("Student"));
    }

    @Test
    void testPdfExporterCreatesValidFile() {
        PdfExporter exporter = new PdfExporter();
        List<TelemetryEvent> events = List.of(
            ExecutionEvent.successfulExecution("Output OK"),
            new EditorInteractionEvent("INSERT")
        );

        exporter.export(events, TEST_PROJECT);

        assertTrue(pdfFile.exists(), "PDF file should be created");
        assertTrue(pdfFile.length() > 0, "PDF file should not be empty");
    }

    @Test
    void testExportPackageListenerTriggersExportersAndClearsStorage() throws Exception {
        TelemetryStorage storage = new TelemetryStorage();
        storage.addEvent(CompilationEvent.successfulCompilation());

        TelemetryExporter mockExporter = mock(TelemetryExporter.class);
        ExportPackageListener listener = new ExportPackageListener(storage, List.of(mockExporter));

        PackageEvent mockEvent = mock(PackageEvent.class);
        BPackage mockPackage = mock(BPackage.class);
        when(mockEvent.getPackage()).thenReturn(mockPackage);
        when(mockPackage.getDir()).thenReturn(new File(TEST_PROJECT));

        listener.packageClosing(mockEvent);

        verify(mockExporter, times(1)).export(anyList(), eq(TEST_PROJECT));
        assertTrue(storage.getEventAndClear().isEmpty(), "Storage should be cleared after export");
    }
}