package com.uwi.comp3607;

import java.io.File;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
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
import bluej.extensions2.BProject;
import bluej.extensions2.event.PackageEvent;

public class TelemetryExporterTest {
    private static final String TEST_PROJECT = "TestProject";
    private File jsonFile;
    private File pdfFile;

    private void setReportFiles(List<TelemetryEvent> events) {

        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

        String timestamp =
            events.get(0).getTimestamp().format(formatter);

        jsonFile = new File(
            TEST_PROJECT + "_" + timestamp + "_Telemetry.json"
        );

        pdfFile = new File(
            TEST_PROJECT + "_" + timestamp + "_Telemetry.pdf"
        );
    }

    @AfterEach
    void tearDown() {
        if (jsonFile != null && jsonFile.exists()) {
            jsonFile.delete();
        }

        if (pdfFile != null && pdfFile.exists()) {
            pdfFile.delete();
        }
    }

    @Test
    void testJsonExporterCreatesValidFile() throws Exception {
        JsonExporter exporter = new JsonExporter();
        List<TelemetryEvent> events = List.of(
            CompilationEvent.successfulCompilation(),
            LifecycleEvent.classCreated("Student")
        );

        setReportFiles(events);
        exporter.export(events, TEST_PROJECT, new File("."));

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

        setReportFiles(events);
        exporter.export(events, TEST_PROJECT, new File("."));

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
        BProject mockProject = mock(BProject.class);

        when(mockPackage.getProject()).thenReturn(mockProject);
        when(mockProject.getDir()).thenReturn(new File(TEST_PROJECT));

        listener.packageClosing(mockEvent);

        verify(mockExporter, times(1))
            .export(
                anyList(),
                eq(TEST_PROJECT),
                eq(new File(TEST_PROJECT))
            );
        assertTrue(storage.getEventAndClear().isEmpty(), "Storage should be cleared after export");
    }

    @Test
    void testFailedExportDoesNotClearStorage() throws Exception {

        TelemetryStorage storage = new TelemetryStorage();

        storage.addEvent(CompilationEvent.successfulCompilation());

        TelemetryExporter failingExporter = mock(TelemetryExporter.class);

        doThrow(new RuntimeException("Export failed"))
            .when(failingExporter)
            .export(
                anyList(),
                eq(TEST_PROJECT),
                eq(new File(TEST_PROJECT))
            );

        ExportPackageListener listener = new ExportPackageListener(storage, List.of(failingExporter));

        PackageEvent mockEvent = mock(PackageEvent.class);
        BPackage mockPackage = mock(BPackage.class);

        when(mockEvent.getPackage()).thenReturn(mockPackage);
        BProject mockProject = mock(BProject.class);

        when(mockPackage.getProject()).thenReturn(mockProject);
        when(mockProject.getDir()).thenReturn(new File(TEST_PROJECT));

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

        setReportFiles(events);
        exporter.export(events, TEST_PROJECT, new File("."));

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

        setReportFiles(events);
        exporter.export(events, TEST_PROJECT, new File("."));

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("Compilation Error Breakdown"));
            assertTrue(text.contains("Error Category"));
            assertTrue(text.contains("Frequency"));
            assertTrue(text.contains("ERR_SYNTAX"));
        }
    }

    @Test
    void testPdfDoesNotExceedTwoPages() throws Exception {

        PdfExporter exporter = new PdfExporter();

        List<TelemetryEvent> events = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            events.add(
                new EditorInteractionEvent("INSERT_" + i)
            );
        }

        setReportFiles(events);
        exporter.export(events, TEST_PROJECT, new File("."));

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            assertEquals(2, document.getNumberOfPages());

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("Session Summary Metrics"));
            assertTrue(text.contains("Compilation Error Breakdown"));
            assertTrue(text.contains("Class Creation Timeline"));
            assertTrue(text.contains("Estimated Coding Activity"));
            assertTrue(text.contains("Active Coding:"));
            assertTrue(text.contains("Idle Time:"));
        }
    }

    @Test
    void testPdfContainsClassCreationTimeline() throws Exception {

        PdfExporter exporter = new PdfExporter();

        List<TelemetryEvent> events = new ArrayList<>();

        events.add(LifecycleEvent.classCreated("Student"));
        events.add(CompilationEvent.successfulCompilation());

        setReportFiles(events);
        exporter.export(events, TEST_PROJECT, new File("."));

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            assertEquals(2, document.getNumberOfPages());

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("Class Creation Timeline"));
            assertTrue(text.contains("CLASS_CREATED on class Student"));
        }
    }

    @Test
    void testJsonContainsCompilationIntervals() throws Exception {

        JsonExporter exporter = new JsonExporter();

        List<TelemetryEvent> events = new ArrayList<>();

        events.add(CompilationEvent.successfulCompilation());

        events.add(CompilationEvent.failedCompilation(
            "COMPILE_ATTEMPT_FAILED",
            "Compilation failed",
            -1
        ));

        setReportFiles(events);
        exporter.export(events, TEST_PROJECT, new File("."));

        String content = Files.readString(jsonFile.toPath());

        JsonObject root = JsonParser
            .parseString(content)
            .getAsJsonObject();

        assertTrue(root.has("compilationIntervalsSeconds"));

        assertEquals(
            1,
            root.getAsJsonArray("compilationIntervalsSeconds").size()
        );
    }
}