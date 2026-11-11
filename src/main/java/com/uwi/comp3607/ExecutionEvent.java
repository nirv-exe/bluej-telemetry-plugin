package com.uwi.comp3607;

public class ExecutionEvent extends TelemetryEvent{
    private final boolean success;
    private final String exceptionThrown;

    //Private constructor so objects can be created using only the factory methods
    private ExecutionEvent(boolean success, String exceptionThrown){
        super("EXECUTION");
        this.success = success;
        this.exceptionThrown = exceptionThrown;
    }

    //Factory Method for successful execution
    public static ExecutionEvent successfulExecution(){
        return new ExecutionEvent(true, null);
    }

    //Factory Method for a failed execution
    public static ExecutionEvent failedExecution(String exceptionThrown){
        return new ExecutionEvent(false, exceptionThrown);
    }

    public boolean getSuccess(){
        return this.success;
    }

    public String getExceptionThrown(){
        return this.exceptionThrown;
    }
}
