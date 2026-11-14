package com.uwi.comp3607;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.event.PackageEvent;

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


    
    @Test
    public void testDifferentProxiesDoNotRegisterSameEditorTwice() throws Exception {
        JavaEditor firstProxy = mock(JavaEditor.class);
        JavaEditor secondProxy = mock(JavaEditor.class);

        when(bPackage.getClasses())
            .thenReturn(new BClass[]{bClass});

        when(bClass.getJavaEditor())
            .thenReturn(firstProxy, secondProxy);

        // Simulate an editor that already has a listener
        when(secondProxy.getProperty(anyString()))
            .thenReturn(new Object());

        registrationListener.packageOpened(packageEvent);
        registrationListener.checkForEditors();

        verify(firstProxy, times(1))
            .addDocumentListener(any(EditorDocumentListener.class));

        verify(secondProxy, never())
            .addDocumentListener(any(EditorDocumentListener.class));

        assertEquals(1, registrationListener.getTrackedEditors().size());
    }

    @Test
    public void testEditorListenersRemovedWhenPackageCloses() throws Exception {
        when(bPackage.getClasses())
            .thenReturn(new BClass[]{bClass});

        registrationListener.packageOpened(packageEvent);

        registrationListener.packageClosing(packageEvent);

        verify(editor, times(1))
            .removeDocumentListener(
                any(EditorDocumentListener.class)
            );

        assertTrue(
            registrationListener.getTrackedEditors().isEmpty()
        );
    }
}
