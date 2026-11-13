package com.uwi.comp3607;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

public class EditorRegistrationListener implements PackageListener {

    private final TelemetryStorage storage;

    private final Map<JavaEditor, EditorActivityTracker> trackedEditors =
            new IdentityHashMap<>();

    private BPackage activePackage;

    public EditorRegistrationListener(TelemetryStorage storage) {

        this.storage = storage;
    }

    @Override
    public void packageOpened(PackageEvent event) {

        activePackage = event.getPackage();
        checkForEditors();
    }

    @Override
    public void packageClosing(PackageEvent event) {

        activePackage = null;
    }

    public void checkForEditors() {

        if (activePackage == null) {
            return;
        }

        try {
            for (BClass bClass : activePackage.getClasses()) {
                registerEditor(bClass);
            }
        } catch (Exception e) {
            System.out.println("Error checking editors: " + e.getMessage());
        }
    }

    private void registerEditor(BClass bClass) {

        try {
            JavaEditor editor = bClass.getJavaEditor();

            if (editor == null || trackedEditors.containsKey(editor)) {
                return;
            }

            EditorActivityTracker tracker = new EditorActivityTracker();
            EditorDocumentListener listener =
                    new EditorDocumentListener(tracker, storage);

            editor.addDocumentListener(listener);
            trackedEditors.put(editor, tracker);

            System.out.println("Editor tracking registered: "
                    + bClass.getName());

        } catch (Exception e) {
            System.out.println("Editor registration failed: "
                    + e.getMessage());
        }
    }

    public Map<JavaEditor, EditorActivityTracker> getTrackedEditors() {

        return Collections.unmodifiableMap(trackedEditors);
    }
}
