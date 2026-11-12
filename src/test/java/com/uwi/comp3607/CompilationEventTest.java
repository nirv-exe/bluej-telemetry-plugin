package com.uwi.comp3607;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CompilationEventTest {

    @Test
    public void testSuccessfulCompilation() {
        CompilationEvent event = CompilationEvent.successfulCompilation();
        
        assertTrue(event.isSuccess());
        assertNull(event.getErrorCode());
        assertNull(event.getErrorMessage());
        assertEquals(-1, event.getAffectedLineNumber());
        assertEquals("COMPILATION", event.getEventType());
        assertNotNull(event.getTimestamp());
    }

    @Test
    public void testFailedCompilation() {
        CompilationEvent event = CompilationEvent.failedCompilation("ERR_SYNTAX", "Missing semicolon", 14);
        
        assertFalse(event.isSuccess());
        assertEquals("ERR_SYNTAX", event.getErrorCode());
        assertEquals("Missing semicolon", event.getErrorMessage());
        assertEquals(14, event.getAffectedLineNumber());
    }
}