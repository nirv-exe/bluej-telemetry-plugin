package com.uwi.comp3607;

/**
 * Stores an execution result, its creation time, and the supplied summary text.
 * The execution listener provides method and return-value summaries and general
 * failure descriptions. These summaries do not contain full console output or
 * exact runtime exception messages.
 */
public class ExecutionEvent extends TelemetryEvent{
    /**
     * Whether the recorded execution completed successfully.
     */
    private final boolean success;
    /**
     * Supplied failure description, or {@code null} for a successful execution.
     */
    private final String exceptionThrown;
    /**
     * Caller-supplied execution summary; this field does not itself capture console output.
     */
    private final String consoleOutput;   

    //Private constructor so objects can be created using only the factory methods
    /**
     * Initializes an execution result with the supplied outcome and descriptive text.
     *
     * @param success whether execution completed successfully
     * @param exceptionThrown the failure description, or {@code null} when absent
     * @param consoleOutput the caller-supplied execution summary
     */
    private ExecutionEvent(boolean success, String exceptionThrown, String consoleOutput){
        super("EXECUTION");
        this.success = success;
        this.exceptionThrown = exceptionThrown;
        this.consoleOutput = consoleOutput;
    }

    //Factory Method for successful execution
    /**
     * Creates a successful execution event without a failure description.
     *
     * @param consoleOutput the caller-supplied execution summary
     * @return a new successful execution event with a {@code null} failure description
     */
    public static ExecutionEvent successfulExecution(String consoleOutput){
        return new ExecutionEvent(true, null, consoleOutput);
    }

    //Factory Method for a failed execution
    /**
     * Creates an unsuccessful execution event with the supplied failure description.
     * The description is stored as text without extracting exception details.
     *
     * @param exceptionThrown the caller-supplied failure description
     * @param consoleOutput the caller-supplied execution summary
     * @return a new unsuccessful execution event
     */
    public static ExecutionEvent failedExecution(String exceptionThrown, String consoleOutput){
        return new ExecutionEvent(false, exceptionThrown, consoleOutput);
    }

    /**
     * Returns the recorded execution outcome.
     *
     * @return {@code true} if execution was recorded as successful
     */
    public boolean getSuccess(){
        return this.success;
    }

    /**
     * Returns the supplied failure description.
     *
     * @return the failure description, or {@code null} when absent
     */
    public String getExceptionThrown(){
        return this.exceptionThrown;
    }

    /**
     * Returns the execution summary supplied when the event was created.
     *
     * @return the stored summary text
     */
    public String  getConsoleOutputSummary(){
        return this.consoleOutput;
    }

    /**
     * Formats the outcome and summary, including the failure description if unsuccessful.
     *
     * @return the execution result description
     */
    @Override
    public String output() {
        if (success) {
            return "Execution succeeded: " + consoleOutput;
        }
        return "Execution failed (" + exceptionThrown + "): " + consoleOutput;
    }
}
