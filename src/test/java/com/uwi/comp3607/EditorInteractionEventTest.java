package com.uwi.comp3607;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class EditorInteractionEventTest {
    
    @Test

    void shouldCreateEditorInteractionEvent(){

        EditorInteractionEvent event = new EditorInteractionEvent("student001", "INSERT");

        assertEquals("EDITOR_INTERACTION", event.getEventType());
        
        assertEquals("student001", event.getStudentId());

        assertEquals("INSERT", event.getAction());

        assertNotNull(event.getTimestamp());
    }
}
