package com.uwi.comp3607;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

public class CompilationListener implements CompileListener{
    private final TelemetryStorage storage; 

    public CompilationListener(TelemetryStorage storage){
        this.storage = storage;
    }

    @Override
    public void compileStarted(CompileEvent event) {
    }

    @Override
    public void compileSucceeded(CompileEvent event) {
        if (!event.isUserGeneratedCompilation()) {
            return;
        }
        CompilationEvent success = CompilationEvent.successfulCompilation(); 
        storage.addEvent(success);
    }

    @Override
    public void compileFailed(CompileEvent event) {
        if (!event.isUserGeneratedCompilation()) {
            return;
        }
        // Tracks the overall failed attempt for your total_compilations metric
        CompilationEvent compEvent = CompilationEvent.failedCompilation("COMPILE_ATTEMPT_FAILED", "The compilation process finished with errors.", -1);
        storage.addEvent(compEvent);
    }

    @Override
    public void compileWarning(CompileEvent event) {}

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
