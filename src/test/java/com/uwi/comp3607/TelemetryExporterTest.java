package com.uwi.comp3607;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    @Test
    void testFailedExportDoesNotClearStorage() throws Exception {

        TelemetryStorage storage = new TelemetryStorage();

        storage.addEvent(CompilationEvent.successfulCompilation());

        TelemetryExporter failingExporter = mock(TelemetryExporter.class);

        doThrow(new RuntimeException("Export failed")).when(failingExporter).export(anyList(), eq(TEST_PROJECT));

        ExportPackageListener listener = new ExportPackageListener(storage, List.of(failingExporter));

        PackageEvent mockEvent = mock(PackageEvent.class);
        BPackage mockPackage = mock(BPackage.class);

        when(mockEvent.getPackage()).thenReturn(mockPackage);
        when(mockPackage.getDir()).thenReturn(new File(TEST_PROJECT));

        listener.packageClosing(mockEvent);

        // Events must still be available after failure
        assertEquals(1, storage.getEvents().size());
    }


    @Test
    void testCompilationErrorJsonFields() throws Exception {

        JsonExporter exporter = new JsonExporter();

        List<TelemetryEvent> events = List.of(
            CompilationEvent.failedCompilation(
                "ERR_SYNTAX",
                "Missing semicolon",
                10
            )
        );

        exporter.export(events, TEST_PROJECT);

        String content = Files.readString(jsonFile.toPath());

        JsonObject root = JsonParser
            .parseString(content)
            .getAsJsonObject();

        JsonObject compilation =
            root.getAsJsonArray("events")
                .get(0)
                .getAsJsonObject();

        assertEquals(
            "ERR_SYNTAX",
            compilation.get("errorCode").getAsString()
        );

        assertEquals(
            "Missing semicolon",
            compilation.get("errorMessage").getAsString()
        );

        assertEquals(
            10,
            compilation.get("affectedLineNumber").getAsInt()
        );

        assertFalse(
            compilation.get("success").getAsBoolean()
        );
    }


    @Test
    void testPdfContainsErrorBreakdown() throws Exception {

        PdfExporter exporter = new PdfExporter();

        List<TelemetryEvent> events = List.of(
            CompilationEvent.failedCompilation(
                "ERR_SYNTAX", "Missing semicolon", 10
            ),
            CompilationEvent.failedCompilation(
                "ERR_SYNTAX", "Missing bracket", 15
            )
        );

        exporter.export(events, TEST_PROJECT);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("Compilation Error Breakdown"));
            assertTrue(text.contains("ERR_SYNTAX: 2"));
        }
    }


    @Test
    void testPdfContainsClassActivityTimeline() throws Exception {

        PdfExporter exporter = new PdfExporter();

        List<TelemetryEvent> events = List.of(
            LifecycleEvent.classCreated("Student"),
            new ClassModificationEvent("Student"),
            CompilationEvent.successfulCompilation()
        );

        exporter.export(events, TEST_PROJECT);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains(
                "Event Timeline & Class Progression"
            ));

            assertTrue(text.contains(
                "CLASS_CREATED on class Student"
            ));

            assertTrue(text.contains(
                "Class (Student) modified."
            ));

            assertTrue(text.contains("Compilation succeeded."));
        }
    }


    @Test
    void testPdfCreatesMultiplePages() throws Exception {

        PdfExporter exporter = new PdfExporter();

        List<TelemetryEvent> events = new ArrayList<>();

        // Create enough events to require more than one page
        for (int i = 0; i < 60; i++) {
            events.add(
                new EditorInteractionEvent("INSERT_" + i)
            );
        }

        exporter.export(events, TEST_PROJECT);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            // Verify that multiple pages were created
            assertTrue(document.getNumberOfPages() > 1);

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Verify events from the beginning and end exist
            assertTrue(text.contains("INSERT_0"));
            assertTrue(text.contains("INSERT_59"));
        }
    }

}