package com.uwi.comp3607;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a compilation event recorded in BlueJ.
 *
 * Stores whether a compilation was successful, along with
 * error information such as the error code, message, and
 * affected line number when a compilation fails.
 *
 * Provides static factory methods for creating successful
 * and failed compilation events.
 */
public class CompilationEvent extends TelemetryEvent{
    /**
     * Whether this event represents a successful compilation.
     */
    private final boolean success;
    /**
     * Supplied failure category, or {@code null} for a successful compilation.
     */
    private final String errorCode;
    /**
     * Supplied failure description, or {@code null} for a successful compilation.
     */
    private final String errorMessage;
    /**
     * The affected line number; a negative value means no line was provided.
     */
    private final int affectedLineNumber;

    //Private constructor so objects can only be created using factory methods
    /**
     * Initializes a timestamped compilation result with the supplied details.
     *
     * @param success whether the compilation succeeded
     * @param errorCode the failure category, or {@code null} when absent
     * @param errorMessage the failure description, or {@code null} when absent
     * @param affectedLineNumber the affected line number, or a negative value when unspecified
     */
    private CompilationEvent(boolean success, String errorCode, String errorMessage, int affectedLineNumber){
        super("COMPILATION");
        this.success = success; 
        this.errorCode = errorCode;
        this.errorMessage = errorMessage; 
        this.affectedLineNumber = affectedLineNumber; 
    }

    //Factory method for a successful compilation
    /**
     * Creates a successful compilation event without failure details.
     * The error code and message are {@code null}, and the line number is {@code -1}.
     *
     * @return a new successful compilation event
     */
    public static CompilationEvent successfulCompilation(){
        return new CompilationEvent(true, null, null, -1);
    }

    //Factory method for failed compilation
    /**
     * Creates a failed compilation event with the given details.
     * {@code COMPILE_ATTEMPT_FAILED} marks an overall failed attempt.
     * Any other category, including {@code null}, counts as a compilation error
     * in reports. The category is stored as given, without reading compiler codes.
     *
     * @param errorCode the failure category, or {@code null} if not provided
     * @param errorMessage the failure message, or {@code null} if not provided
     * @param affectedLineNumber the affected line number, or a negative value if unknown
     * @return a new failed compilation event
     */
    public static CompilationEvent failedCompilation(String errorCode, String errorMessage, int affectedLineNumber){
        return new CompilationEvent(false, errorCode, errorMessage, affectedLineNumber);
    }

    /**
     * Returns the stored compilation outcome.
     *
     * @return {@code true} if this event represents a successful compilation
     */
    public boolean isSuccess() { return success; }
    /**
     * Returns the supplied failure category without translating it.
     *
     * @return the failure category, or {@code null} when absent
     */
    public String getErrorCode() { return errorCode; }
    /**
     * Returns the failure description supplied at creation.
     *
     * @return the failure message, or {@code null} when absent
     */
    public String getErrorMessage() { return errorMessage; }
    /**
     * Returns the line number supplied at creation.
     *
     * @return the affected line number, or a negative value when unspecified
     */
    public int getAffectedLineNumber() { return affectedLineNumber; }

    /**
     * Formats the compilation outcome, including stored failure details when unsuccessful.
     *
     * @return the compilation result description
     */
    @Override
    public String output() {
        if (success) {
            return "Compilation succeeded.";
        }
        return "Compilation failed [" + errorCode + "] at line " + affectedLineNumber + ": " + errorMessage;
    }

        /**
         * Classifies a successful result as a compilation attempt for report aggregation.
         *
         * @return {@code true} if the stored outcome is successful
         */
        @Override
    public boolean isSuccessfulCompilation() {
        return success;
    }

    /**
     * Distinguishes an overall failed attempt from an individual compiler error.
     *
     * @return {@code true} if unsuccessful with category {@code COMPILE_ATTEMPT_FAILED}
     */
    @Override
    public boolean isFailedCompilationAttempt() {
        return !success &&
            "COMPILE_ATTEMPT_FAILED".equals(errorCode);
    }

    /**
     * Identifies unsuccessful results other than overall failed-attempt markers.
     *
     * @return {@code true} if unsuccessful with any category other than
     *         {@code COMPILE_ATTEMPT_FAILED}, including a {@code null} category
     */
    @Override
    public boolean isCompilationError() {
        return !success &&
            !"COMPILE_ATTEMPT_FAILED".equals(errorCode);
    }


    /**
     * Returns compilation details for JSON export. Always includes success status.
     * Includes the error category and message when they are not null, and the line
     * number when it is zero or greater.
     *
     * @return a new mutable map of the available compilation details
     */
    @Override
    public Map<String, Object> getAdditionalDetails() {

        Map<String, Object> details = new HashMap<String, Object>();

        details.put("success", success);

        if (errorCode != null) {
            details.put("errorCode", errorCode);
        }

        if (errorMessage != null) {
            details.put("errorMessage", errorMessage);
        }

        if (affectedLineNumber >= 0) {
            details.put("affectedLineNumber", affectedLineNumber);
        }

        return details;
    }
}
