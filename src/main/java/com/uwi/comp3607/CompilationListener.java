package com.uwi.comp3607;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

/**
 * Listens for compilation events in BlueJ and records
 * successful and failed user-generated compilations.
 *
 * Compilation events are stored in TelemetryStorage
 * for later analysis and report generation.
 */
public class CompilationListener implements CompileListener{

    /** Stores compilation events recorded by this listener */
    private final TelemetryStorage storage; 

    /**
     * Creates a compilation listener.
     *
     * @param storage the storage used to record telemetry events
     */

    public CompilationListener(TelemetryStorage storage){
        this.storage = storage;
    }


    /**
     * Receives notification when a compilation begins
     * No event is recorded at this stage.
     *
     * @param event the BlueJ compilation event
     */
    @Override
    public void compileStarted(CompileEvent event) {
    }

    /**
     * Records a successful user-generated compilation.
     * Automatic compilations are ignored.
     *
     * @param event the BlueJ compilation event
     */
    @Override
    public void compileSucceeded(CompileEvent event) {
        if (!event.isUserGeneratedCompilation()) {
            return;
        }
        CompilationEvent success = CompilationEvent.successfulCompilation(); 
        storage.addEvent(success);
    }

    /**
     * Records a failed user-generated compilation.
     * This event is used when calculating total compilation attempts
     *
     * @param event the BlueJ compilation event
     */
    @Override
    public void compileFailed(CompileEvent event) {
        if (!event.isUserGeneratedCompilation()) {
            return;
        }
        // Tracks the overall failed attempt for your total_compilations metric
        CompilationEvent compEvent = CompilationEvent.failedCompilation("COMPILE_ATTEMPT_FAILED", "The compilation process finished with errors.", -1);
        storage.addEvent(compEvent);
    }

    /**
     * Receives compilation warnings.
     * Warnings are not currently recorded by this listener.
     *
     * @param event the BlueJ compilation event
     */
    @Override
    public void compileWarning(CompileEvent event) {}

    /**
     * Records a compiler error from a user-generated compilation.
     * Stores the error message and affected line number
     * provided by BlueJ. Uses the generic category {@code COMPILER_ERROR},
     * rather than a compiler-specific diagnostic code.
     *
     * @param event the BlueJ compilation event
     */
    @Override
    public void compileError(CompileEvent event) {
        if (!event.isUserGeneratedCompilation()) {
            return;
        }
        String errorCode = "COMPILER_ERROR";
        String errorMessage = event.getErrorMessage();
        int lineNum = event.getErrorLineNumber();

        CompilationEvent compEvent = CompilationEvent.failedCompilation(errorCode, errorMessage, lineNum);
        storage.addEvent(compEvent);
    }
}
