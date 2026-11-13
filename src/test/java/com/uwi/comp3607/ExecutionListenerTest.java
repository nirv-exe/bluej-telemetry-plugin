package com.uwi.comp3607;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bluej.extensions2.event.InvocationFinishedEvent;

public class ExecutionListenerTest {
    private TelemetryStorage storage;
    private ExecutionListener listener;

    @BeforeEach
    public void setUp() {
        // Runs before every test to ensure a clean slate
        storage = new TelemetryStorage();
        listener = new ExecutionListener(storage);
    }

    @Test
    public void testSuccessfulInvocationIsStored() {
        // 1. Arrange: Create a mock BlueJ event and define its behavior
        InvocationFinishedEvent mockEvent = mock(InvocationFinishedEvent.class);
        
        // Tell the mock exactly what to return when the listener calls these methods
        when(mockEvent.getEventType()).thenReturn(InvocationFinishedEvent.EventType.NORMAL_EXIT);
        when(mockEvent.getMethodName()).thenReturn("calculateFare");

        // 2. Act: Trigger the listener manually
        listener.invocationFinished(mockEvent);

        // 3. Assert: Verify the storage captured the event correctly
        List<TelemetryEvent> events = storage.getEventAndClear();
        assertEquals(1, events.size(), "Storage should contain exactly 1 event");
        
        // Cast the base TelemetryEvent to an ExecutionEvent to check specific fields
        ExecutionEvent capturedEvent = (ExecutionEvent) events.get(0);
        assertTrue(capturedEvent.getSuccess());
        assertEquals("Method: calculateFare | No return value", capturedEvent.getConsoleOutputSummary());
    }

    @Test
    public void testCrashedInvocationIsStored() {
        // 1. Arrange: Simulate a crash
        InvocationFinishedEvent mockEvent = mock(InvocationFinishedEvent.class);
        
        when(mockEvent.getEventType()).thenReturn(InvocationFinishedEvent.EventType.EXCEPTION_EXIT);
        when(mockEvent.getMethodName()).thenReturn("divideByZero");
        when(mockEvent.getClassName()).thenReturn("Calculator");

        // 2. Act
        listener.invocationFinished(mockEvent);

        // 3. Assert
        List<TelemetryEvent> events = storage.getEventAndClear();
        ExecutionEvent capturedEvent = (ExecutionEvent) events.get(0);
        
        assertFalse(capturedEvent.getSuccess());
        assertEquals("Runtime exception in class Calculator, method divideByZero", capturedEvent.getExceptionThrown());
    }

    @Test
    public void testTerminatedInvocationIsNotSuccessful() {

        InvocationFinishedEvent mockEvent =
            mock(InvocationFinishedEvent.class);

        when(mockEvent.getEventType()).thenReturn(
            InvocationFinishedEvent.EventType.TERMINATED_EXIT
        );

        when(mockEvent.getMethodName()).thenReturn("main");

        listener.invocationFinished(mockEvent);

        List<TelemetryEvent> events =
            storage.getEventAndClear();

        ExecutionEvent capturedEvent =
            (ExecutionEvent) events.get(0);

        assertFalse(capturedEvent.getSuccess());
    }
}