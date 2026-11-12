package com.uwi.comp3607;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.event.PackageEvent;

public class ProjectLifecycleListenerTest {

    private TelemetryStorage storage;
    private ProjectLifecycleListener listener;

    @BeforeEach
    public void setUp() 
    {
        storage = new TelemetryStorage();
        listener = new ProjectLifecycleListener(storage);
    }

    @Test
    public void testPackageOpened() throws Exception 
    {

        BPackage packageMock = mock(BPackage.class);
        PackageEvent event = mock(PackageEvent.class);
        BClass existingClass = mock(BClass.class);

        when(event.getPackage()).thenReturn(packageMock);
        when(existingClass.getName()).thenReturn("Student");

        when(packageMock.getClasses()).thenReturn(
            new BClass[]{existingClass}
        );

        listener.packageOpened(event);

        listener.checkForClassChanges();

        assertTrue(storage.getEventAndClear().isEmpty());
    }

    @Test
    public void testNewClassDetected() throws Exception {

        BPackage packageMock = mock(BPackage.class);
        PackageEvent event = mock(PackageEvent.class);

        BClass existingClass = mock(BClass.class);
        BClass newClass = mock(BClass.class);

        when(event.getPackage()).thenReturn(packageMock);

        when(existingClass.getName()).thenReturn("ExistingClass");
        when(newClass.getName()).thenReturn("NewClass");

        when(packageMock.getClasses()).thenReturn(
            new BClass[]{existingClass},
            new BClass[]{existingClass, newClass}
        );

        listener.packageOpened(event);

        listener.checkForClassChanges();

        var events = storage.getEventAndClear();

        assertEquals(1, events.size());

        LifecycleEvent createdEvent =
            assertInstanceOf(LifecycleEvent.class, events.get(0));

        assertEquals("CLASS_CREATED", createdEvent.getAction());
        assertEquals("NewClass", createdEvent.getClassName());
    }

    @Test
    public void testDeletedClassDetected() throws Exception {

        BPackage packageMock = mock(BPackage.class);
        PackageEvent event = mock(PackageEvent.class);

        BClass existingClass = mock(BClass.class);
        BClass deletedClass = mock(BClass.class);

        when(event.getPackage()).thenReturn(packageMock);

        when(existingClass.getName()).thenReturn("ExistingClass");
        when(deletedClass.getName()).thenReturn("DeletedClass");

        when(packageMock.getClasses()).thenReturn(
            new BClass[]{existingClass, deletedClass},
            new BClass[]{existingClass}
        );

        listener.packageOpened(event);

        listener.checkForClassChanges();

        var events = storage.getEventAndClear();

        assertEquals(1, events.size());

        LifecycleEvent deletedEvent =
            assertInstanceOf(LifecycleEvent.class, events.get(0));

        assertEquals("CLASS_DELETED", deletedEvent.getAction());
        assertEquals("DeletedClass", deletedEvent.getClassName());
    }

    @Test
    public void testPackageClosed() throws Exception {

        BPackage packageMock = mock(BPackage.class);
        PackageEvent event = mock(PackageEvent.class);

        BClass existingClass = mock(BClass.class);
        BClass newClass = mock(BClass.class);

        when(event.getPackage()).thenReturn(packageMock);

        when(existingClass.getName()).thenReturn("ExistingClass");
        when(newClass.getName()).thenReturn("NewClass");

        when(packageMock.getClasses()).thenReturn(
            new BClass[]{existingClass},
            new BClass[]{existingClass, newClass}
        );

        listener.packageOpened(event);

        listener.packageClosing(event);

        var events = storage.getEventAndClear();

        assertEquals(1, events.size());

        LifecycleEvent createdEvent =
            assertInstanceOf(LifecycleEvent.class, events.get(0));

        assertEquals("CLASS_CREATED", createdEvent.getAction());
        assertEquals("NewClass", createdEvent.getClassName());

        listener.checkForClassChanges();

        assertTrue(storage.getEventAndClear().isEmpty());
    }
}

