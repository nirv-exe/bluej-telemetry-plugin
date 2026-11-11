package com.uwi.comp3607;

import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;

public class ExecutionListener implements InvocationFinishedListener{
    private final TelemetryStorage storage; 

    public ExecutionListener (TelemetryStorage storage){
        this.storage = storage;
    }

    @Override 
    public void invocationFinished(InvocationFinishedEvent event){
        boolean crashed = (event.getEventType() == InvocationFinishedEvent.EventType.EXCEPTION_EXIT);
        String methodName = event.getMethodName();
        String outputSummary = "Console Output Captured for " + methodName;

        ExecutionEvent execEvent;

        if(!crashed){
            execEvent = ExecutionEvent.successfulExecution(outputSummary);
        } else {
            String exceptionDetails = "Runtime Exception occured in method: " + methodName;
            execEvent = ExecutionEvent.failedExecution(exceptionDetails, outputSummary);
        }

        storage.addEvent(execEvent);
    }
}
