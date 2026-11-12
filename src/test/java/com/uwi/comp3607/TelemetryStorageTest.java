package com.uwi.comp3607;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TelemetryStorageTest {
    private TelemetryStorage storage;

    @BeforeEach
    public void setUp() {
        storage = new TelemetryStorage();
    }

    @Test
    public void testAddAndClearEvents() {
        // Arrange: Create dummy events using your factory methods
        CompilationEvent successCompile = CompilationEvent.successfulCompilation();
        ExecutionEvent successExec = ExecutionEvent.successfulExecution("Output OK");
        
        // Act: Add them to storage
        storage.addEvent(successCompile);
        storage.addEvent(successExec);
        
        // Assert: The list should have 2 items
        List<TelemetryEvent> snapshot = storage.getEventAndClear();
        assertEquals(2, snapshot.size(), "Snapshot should retrieve exactly 2 events");
        
        // Assert: Calling it a second time should return an empty list (proving it cleared)
        List<TelemetryEvent> emptySnapshot = storage.getEventAndClear();
        assertEquals(0, emptySnapshot.size(), "Storage should be completely empty after getEventAndClear is called");
    }
}