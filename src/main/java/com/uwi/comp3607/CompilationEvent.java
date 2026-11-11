package com.uwi.comp3607;

public class CompilationEvent extends TelemetryEvent{
    private final boolean success;
    private final String errorCode;
    private final String errorMessage;
    private final int affectedLineNumber;

    //Private constructor so objects can only be created using factory methods
    private CompilationEvent(boolean success, String errorCode, String errorMessage, int affectedLineNumber){
        super("COMPILATION");
        this.success = success; 
        this.errorCode = errorCode;
        this.errorMessage = errorMessage; 
        this.affectedLineNumber = affectedLineNumber; 
    }

    //Factory method for a successful compilation
    public static CompilationEvent successfulCompilation(){
        return new CompilationEvent(true, null, null, -1);
    }

    //Factory method for failed compilation
    public static CompilationEvent failedCompilation(String errorCode, String errorMessage, int affectedLineNumber){
        return new CompilationEvent(false, errorCode, errorMessage, affectedLineNumber);
    }

    public boolean isSuccess() { return success; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public int getAffectedLineNumber() { return affectedLineNumber; }
}
