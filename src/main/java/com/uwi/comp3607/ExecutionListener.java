package com.uwi.comp3607;

import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;

public class ExecutionListener implements InvocationFinishedListener{
    @Override 
    public void invocationFinished(InvocationFinishedEvent event){
        boolean crashed = (event.getEventType() == InvocationFinishedEvent.EventType.EXCEPTION_EXIT);

        String methodName = event.getMethodName();

        ExecutionEvent execEvent;

        if(!crashed){
            execEvent = ExecutionEvent.successfulExecution();
            System.out.println("Recorded successful Execution of: " + methodName);
        } else {
            String exceptionDetails = "Runtime Exception occured in method: " + methodName;
            execEvent = ExecutionEvent.failedExecution(exceptionDetails);

            System.out.println("Recorded failed execution: " + execEvent.getExceptionThrown());
        }


    }
}
