package com.uwi.comp3607;

import java.util.ArrayList;
import java.util.List;

import bluej.extensions2.BClass;
import bluej.extensions2.BPackage;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

/**
 * Detects added and removed classes by comparing current class names with the
 * previously recorded names. Opening a package records its existing classes
 * without creating events. Checks for changes when compilation starts and the
 * package closes. A rename may be recorded as an addition and a removal.
 */
public class ProjectLifecycleListener implements PackageListener {
    /**
     * Shared destination for detected class creation and deletion events.
     */
    private final TelemetryStorage storage;
    /**
     * Class names remembered from the baseline and subsequent successful comparisons.
     */
    private List<String> knownClasses; 
    /**
     * Package currently polled for class changes, or {@code null} when inactive.
     */
    private BPackage activePackage;

    /**
     * Creates a lifecycle listener with an initially empty class-name baseline.
     *
     * @param storage the shared telemetry event store
     */
    public ProjectLifecycleListener(TelemetryStorage storage) {
        this.storage = storage;
        this.knownClasses = new ArrayList<>();
    }

    /**
     * Sets the active package and rebuilds the baseline from its existing classes.
     * Existing names do not generate creation events. Package-access failures are
     * caught and reported with a console message.
     *
     * @param event the BlueJ package-open notification
     */
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

    /**
     * Checks for final class changes before clearing the baseline and active package.
     *
     * @param event the BlueJ package-close notification
     */
    @Override
    public void packageClosing(PackageEvent event) {
        checkForClassChanges();
        this.knownClasses.clear();
        this.activePackage = null;
    }

    // Called when compilation starts to check for class changes
    /**
     * Compares current class names with the remembered baseline and records additions
     * and removals while updating that baseline. Does nothing without an active
     * package and catches and prints failures encountered during polling.
     */
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
