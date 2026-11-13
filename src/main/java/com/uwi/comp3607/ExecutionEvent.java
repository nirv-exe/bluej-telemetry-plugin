package com.uwi.comp3607;

public class ExecutionEvent extends TelemetryEvent{
    private final boolean success;
    private final String exceptionThrown;
    private final String consoleOutput;   

    //Private constructor so objects can be created using only the factory methods
    private ExecutionEvent(boolean success, String exceptionThrown, String consoleOutput){
        super("EXECUTION");
        this.success = success;
        this.exceptionThrown = exceptionThrown;
        this.consoleOutput = consoleOutput;
    }

    //Factory Method for successful execution
    public static ExecutionEvent successfulExecution(String consoleOutput){
        return new ExecutionEvent(true, null, consoleOutput);
    }

    //Factory Method for a failed execution
    public static ExecutionEvent failedExecution(String exceptionThrown, String consoleOutput){
        return new ExecutionEvent(false, exceptionThrown, consoleOutput);
    }

    public boolean getSuccess(){
        return this.success;
    }

    public String getExceptionThrown(){
        return this.exceptionThrown;
    }

    public String  getConsoleOutputSummary(){
        return this.consoleOutput;
    }

    @Override
    public String output() {
        if (success) {
            return "Execution succeeded: " + consoleOutput;
        }
        return "Execution failed (" + exceptionThrown + "): " + consoleOutput;
    }
}
