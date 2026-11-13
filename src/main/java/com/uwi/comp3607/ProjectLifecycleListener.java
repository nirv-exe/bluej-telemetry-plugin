package com.uwi.comp3607;

import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

public class ProjectLifecycleListener implements PackageListener {
    private final TelemetryStorage storage;
    private List<String> knownClasses; 
    private BPackage activePackage;
    private EditorDocumentListener editorListener;

    public ProjectLifecycleListener(TelemetryStorage storage) {
        this.storage = storage;
        this.knownClasses = new ArrayList<>();
    }

    public ProjectLifecycleListener(TelemetryStorage storage, EditorDocumentListener editorListener) {

        this(storage);
        this.editorListener = editorListener;
}

    @Override
    public void packageOpened(PackageEvent event) {
        try {
            this.activePackage = event.getPackage();
            this.knownClasses.clear();
            
            for (BClass bClass : activePackage.getClasses()) {
                knownClasses.add(bClass.getName());
            }
        } catch (Exception e) {
            System.out.println("Error reading package classes.");
        }
    }

    @Override
    public void packageClosing(PackageEvent event) {
        checkForClassChanges();
        this.knownClasses.clear();
        this.activePackage = null;
    }

    // Called manually by CompilationListener
    public void checkForClassChanges() {
        if (activePackage == null) return;

        try {
            List<String> currentClassNames = new ArrayList<>();
            for (BClass bClass : activePackage.getClasses()) {
                currentClassNames.add(bClass.getName());
            }

            // Check for new classes
            for (String currentName : currentClassNames) {
                if (!knownClasses.contains(currentName)) {
                    storage.addEvent(LifecycleEvent.classCreated(currentName));
                    knownClasses.add(currentName); 
                }
            }

            // Check for deleted classes
            List<String> removedNames = new ArrayList<>();
            for (String knownName : knownClasses) {
                if (!currentClassNames.contains(knownName)) {
                    storage.addEvent(LifecycleEvent.classDeleted(knownName));
                    removedNames.add(knownName);
                }
            }
            knownClasses.removeAll(removedNames);

        } catch (Exception e) {
            System.out.println("Error polling class changes.");
        }
    }
}
