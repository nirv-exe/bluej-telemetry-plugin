package com.uwi.comp3607;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bluej.extensions2.BClass;
import bluej.extensions2.event.ClassEvent;

public class ClassDataListenerTest {

    private TelemetryStorage storage;
    private ClassDataListener listener;

    @BeforeEach
    public void setUp() {
        storage = new TelemetryStorage();
        listener = new ClassDataListener(storage);
    }

    @Test
    public void testClassModifiedDetected() {

        BClass classMock = mock(BClass.class);
        ClassEvent event = mock(ClassEvent.class);

        when(event.getBClass()).thenReturn(classMock);
        when(classMock.getName()).thenReturn("Class");
        when(event.isClassCompiled()).thenReturn(false);

        listener.classStateChanged(event);

        var events = storage.getEventAndClear();

        assertEquals(1, events.size());

        ClassModificationEvent modifiedEvent =
            assertInstanceOf(ClassModificationEvent.class, events.get(0));

        assertEquals("Class", modifiedEvent.getName());
        assertEquals("Class (Class) modified.",modifiedEvent.output());
    }

    @Test
    public void testCompiledClassIsNotRecordedAsModified() {

        BClass classMock = mock(BClass.class);
        ClassEvent event = mock(ClassEvent.class);

        when(event.getBClass()).thenReturn(classMock);
        when(classMock.getName()).thenReturn("Student");
        when(event.isClassCompiled()).thenReturn(true);

        listener.classStateChanged(event);

        assertTrue(storage.getEventAndClear().isEmpty());
    }
}

