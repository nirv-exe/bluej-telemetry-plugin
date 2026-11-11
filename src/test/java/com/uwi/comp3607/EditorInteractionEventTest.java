package com.uwi.comp3607;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class EditorInteractionEventTest {
    
    @Test

    void shouldCreateEditorInteractionEvent(){

        EditorInteractionEvent event = new EditorInteractionEvent("INSERT");

        assertEquals("EDITOR_INTERACTION", event.getEventType());

        assertEquals("INSERT", event.getAction());

        assertNotNull(event.getTimestamp());
    }
}
