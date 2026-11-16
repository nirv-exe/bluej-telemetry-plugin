package com.uwi.comp3607;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.editor.JavaEditor;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

/**
 * Finds Java editors in the most recently opened package and tracks each editor
 * object with a document listener and activity tracker. Checks for editors when
 * a package opens and compilation starts. When a package closes, attempts to
 * remove the listeners and clears its tracking records.
 */
public class EditorRegistrationListener implements PackageListener {

    /**
     * Shared destination passed to each registered document listener.
     */
    private final TelemetryStorage storage;

    /**
     * Editor property key marking an existing telemetry document-listener registration.
     */
    private static final String REGISTRATION_KEY = "com.uwi.comp3607.telemetry.documentListener";
    /**
     * Registered document listeners, matched by editor object identity.
     */
    private final Map<JavaEditor, EditorDocumentListener> registeredListeners = new IdentityHashMap<>();

    /**
     * Per-editor activity trackers keyed by editor object identity.
     */
    private final Map<JavaEditor, EditorActivityTracker> trackedEditors = new IdentityHashMap<>();

    /**
     * Most recently opened package to scan, or {@code null} when no package is active.
     */
    private BPackage activePackage;

    /**
     * Creates an editor registrar using the shared event store.
     *
     * @param storage the destination for events recorded by editor document listeners
     */
    public EditorRegistrationListener(TelemetryStorage storage) {

        this.storage = storage;
    }

    /**
     * Sets the active package and immediately checks its classes for Java editors.
     *
     * @param event the BlueJ package-open notification
     */
    @Override
    public void packageOpened(PackageEvent event) {

        activePackage = event.getPackage();
        checkForEditors();
    }

    /**
     * Attempts to remove every registered listener and its editor property marker.
     * Cleanup exceptions are printed per editor; local maps and the active package
     * are cleared even when an editor cleanup fails.
     *
     * @param event the BlueJ package-close notification
     */
    @Override
    public void packageClosing(PackageEvent event) {
        for (Map.Entry<JavaEditor, EditorDocumentListener> entry: registeredListeners.entrySet()) {
            try {
                JavaEditor editor = entry.getKey();
                EditorDocumentListener listener = entry.getValue();

                editor.removeDocumentListener(listener);
                editor.setProperty(REGISTRATION_KEY, null);

            } catch (Exception e) {
                System.out.println("Editor cleanup failed: " + e.getMessage());
            }
        }

        registeredListeners.clear();
        trackedEditors.clear();
        activePackage = null;
    }

    /**
     * Scans the active package's classes and attempts registration for each editor.
     * Returns immediately when no package is active and prints package-access failures.
     */
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

    /**
     * Attaches a tracker and document listener if the class has an unregistered Java editor.
     * Skips absent editors and editors already tracked or marked by the registration
     * property. Registration exceptions are caught and printed.
     *
     * @param bClass the BlueJ class whose Java editor should be checked
     */
    private void registerEditor(BClass bClass) {

        try {
            JavaEditor editor = bClass.getJavaEditor();

            if (editor == null) {
                return;
            }

            if (trackedEditors.containsKey(editor) ||
                editor.getProperty(REGISTRATION_KEY) != null) {
                return;
            }

            EditorActivityTracker tracker = new EditorActivityTracker();
            EditorDocumentListener listener = new EditorDocumentListener(tracker, storage);

            editor.addDocumentListener(listener);
            editor.setProperty(REGISTRATION_KEY, listener);
            trackedEditors.put(editor, tracker);
            registeredListeners.put(editor, listener);

            System.out.println("Editor tracking registered: "
                    + bClass.getName());

        } catch (Exception e) {
            System.out.println("Editor registration failed: "
                    + e.getMessage());
        }
    }

    /**
     * Returns a view of the registered editors and their activity trackers.
     * Entries cannot be added or removed through this view. It reflects later
     * registrations and cleanup, and the trackers themselves can still be used.
     *
     * @return an unmodifiable view of the editor-to-tracker map that reflects changes
     */
    public Map<JavaEditor, EditorActivityTracker> getTrackedEditors() {

        return Collections.unmodifiableMap(trackedEditors);
    }
}
