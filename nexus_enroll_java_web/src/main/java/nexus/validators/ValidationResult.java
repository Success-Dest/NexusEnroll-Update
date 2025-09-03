package nexus.validators;

/**
 * Represents the result of a validation operation in the Nexus Enrollment system.
 * 
 * <p>This class encapsulates both the success/failure status of a validation check
 * and an associated message that describes the validation result. For successful
 * validations, the message is typically a simple "OK", while for failed validations,
 * the message explains the reason for the failure.</p>
 * 
 * <p>This class is immutable and instances are created using the static factory methods
 * {@link #success()} and {@link #fail(String)}.</p>
 */
public class ValidationResult {
    /** Flag indicating whether the validation passed (true) or failed (false). */
    public final boolean ok;
    
    /** Message describing the validation result. */
    public final String message;

    /**
     * Private constructor to enforce use of static factory methods.
     *
     * @param ok Whether the validation was successful
     * @param message Message describing the validation result
     */
    private ValidationResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    /**
     * Creates a ValidationResult representing a successful validation.
     *
     * @return A ValidationResult with ok=true and message="OK"
     */
    public static ValidationResult success() {
        return new ValidationResult(true, "OK");
    }

    /**
     * Creates a ValidationResult representing a failed validation with the specified error message.
     *
     * @param msg The error message explaining why validation failed
     * @return A ValidationResult with ok=false and the provided error message
     */
    public static ValidationResult fail(String msg) {
        return new ValidationResult(false, msg);
    }
}

