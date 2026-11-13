package com.uwi.comp3607;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.event.PackageEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EditorRegistrationListenerTest {

    private TelemetryStorage storage;
    private EditorRegistrationListener registrationListener;

    private BClass bClass;
    private BPackage bPackage;
    private JavaEditor editor;
    private PackageEvent packageEvent;

    @BeforeEach
    public void setUp() throws Exception {

        storage = new TelemetryStorage();
        registrationListener = new EditorRegistrationListener(storage);

        bClass = mock(BClass.class);
        bPackage = mock(BPackage.class);
        editor = mock(JavaEditor.class);
        packageEvent = mock(PackageEvent.class);

        when(packageEvent.getPackage()).thenReturn(bPackage);
        when(bClass.getJavaEditor()).thenReturn(editor);
    }

    @Test
    public void testEditorRegisteredWhenPackageOpened() throws Exception {

        when(bPackage.getClasses()).thenReturn(new BClass[]{bClass});

        registrationListener.packageOpened(packageEvent);

        assertEquals(1, registrationListener.getTrackedEditors().size());
        assertTrue(registrationListener.getTrackedEditors().containsKey(editor));

        verify(editor, times(1))
                .addDocumentListener(any(EditorDocumentListener.class));
    }

    @Test
    public void testEditorNotRegisteredTwice() throws Exception {

        when(bPackage.getClasses()).thenReturn(new BClass[]{bClass});

        registrationListener.packageOpened(packageEvent);
        registrationListener.checkForEditors();

        assertEquals(1, registrationListener.getTrackedEditors().size());

        verify(editor, times(1))
                .addDocumentListener(any(EditorDocumentListener.class));
    }

    @Test
    public void testNullEditorIsIgnored() throws Exception {

        when(bPackage.getClasses()).thenReturn(new BClass[]{bClass});
        when(bClass.getJavaEditor()).thenReturn(null);

        registrationListener.packageOpened(packageEvent);

        assertTrue(registrationListener.getTrackedEditors().isEmpty());
    }

    @Test
    public void testNoRegistrationAfterPackageClosing() throws Exception {

        when(bPackage.getClasses()).thenReturn(new BClass[]{bClass});

        registrationListener.packageOpened(packageEvent);
        registrationListener.packageClosing(packageEvent);

        registrationListener.checkForEditors();

        verify(editor, times(1))
                .addDocumentListener(any(EditorDocumentListener.class));
    }

    @Test
    public void testDifferentEditorsHaveSeparateTrackers() throws Exception {

        BClass firstClass = mock(BClass.class);
        BClass secondClass = mock(BClass.class);

        JavaEditor firstEditor = mock(JavaEditor.class);
        JavaEditor secondEditor = mock(JavaEditor.class);

        when(firstClass.getJavaEditor()).thenReturn(firstEditor);
        when(secondClass.getJavaEditor()).thenReturn(secondEditor);

        when(bPackage.getClasses()).thenReturn(
            new BClass[]{firstClass, secondClass});

        registrationListener.packageOpened(packageEvent);

        assertEquals(2, registrationListener.getTrackedEditors().size());

        EditorActivityTracker firstTracker =
            registrationListener.getTrackedEditors().get(firstEditor);

        EditorActivityTracker secondTracker =
            registrationListener.getTrackedEditors().get(secondEditor);

        assertNotNull(firstTracker);
        assertNotNull(secondTracker);
        assertNotSame(firstTracker, secondTracker);

        verify(firstEditor, times(1)).addDocumentListener(any(EditorDocumentListener.class));
        verify(secondEditor, times(1)).addDocumentListener(any(EditorDocumentListener.class));
}
}
