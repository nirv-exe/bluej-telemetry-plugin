package com.uwi.comp3607;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

public class CompilationListener implements CompileListener{
    @Override
    public void compileStarted(CompileEvent event) {
    }

    @Override
    public void compileSucceeded(CompileEvent event) {
        CompilationEvent success = CompilationEvent.successfulCompilation(); 

        // TEMPORARY: Print to console instead of saving to storage
        System.out.println("Recorded Successful Compilation at: " + success.getTimestamp());
    }

    @Override
    public void compileFailed(CompileEvent event) {
    }

    @Override
    public void compileWarning(CompileEvent event) {}

    @Override
    public void compileError(CompileEvent event) {
        String errorCode = "ERR_SYNTAX";
        String errorMessage = event.getErrorMessage();
        int lineNum = event.getErrorLineNumber();

        CompilationEvent compEvent = CompilationEvent.failedCompilation(errorCode, errorMessage, lineNum);
        System.out.println("Recorded Failed Compilation at: " + compEvent.getTimestamp());
    }
}
