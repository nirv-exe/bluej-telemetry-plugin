package com.uwi.comp3607;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bluej.extensions2.event.CompileEvent;

public class CompilationListenerTest {
    private TelemetryStorage storage;
    private CompilationListener listener;

    @BeforeEach
    public void setUp() {
        storage = new TelemetryStorage();
        listener = new CompilationListener(storage);
    }

    @Test
    public void testCompileSucceeded() {
        // Arrange
        CompileEvent mockEvent = mock(CompileEvent.class);

        // Act
        listener.compileSucceeded(mockEvent);

        // Assert
        List<TelemetryEvent> events = storage.getEventAndClear();
        assertEquals(1, events.size(), "Storage should contain 1 success event");
        
        CompilationEvent capturedEvent = (CompilationEvent) events.get(0);
        assertTrue(capturedEvent.isSuccess());
        assertEquals(-1, capturedEvent.getAffectedLineNumber());
    }

    @Test
    public void testCompileError() {
        // Arrange
        CompileEvent mockEvent = mock(CompileEvent.class);
        when(mockEvent.getErrorMessage()).thenReturn("missing ';' at end of declaration");
        when(mockEvent.getErrorLineNumber()).thenReturn(25);

        // Act
        listener.compileError(mockEvent);

        // Assert
        List<TelemetryEvent> events = storage.getEventAndClear();
        assertEquals(1, events.size());
        
        CompilationEvent capturedEvent = (CompilationEvent) events.get(0);
        assertFalse(capturedEvent.isSuccess());
        assertEquals("COMPILER_ERROR", capturedEvent.getErrorCode());
        assertEquals("missing ';' at end of declaration", capturedEvent.getErrorMessage());
        assertEquals(25, capturedEvent.getAffectedLineNumber());
    }
    
    @Test
    public void testCompileFailed() {
        // Arrange
        CompileEvent mockEvent = mock(CompileEvent.class);

        // Act
        listener.compileFailed(mockEvent);

        // Assert
        List<TelemetryEvent> events = storage.getEventAndClear();
        assertEquals(1, events.size());
        
        CompilationEvent capturedEvent = (CompilationEvent) events.get(0);
        assertFalse(capturedEvent.isSuccess());
        assertEquals("COMPILE_ATTEMPT_FAILED", capturedEvent.getErrorCode());
    }
}