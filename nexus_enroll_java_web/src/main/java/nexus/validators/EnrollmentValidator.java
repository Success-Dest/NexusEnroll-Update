package nexus.validators;

import nexus.model.Student;
import nexus.model.Section;

/**
 * Interface for validators that check enrollment eligibility in the Nexus Enrollment system.
 * 
 * <p>This interface defines a common contract for all validators that check whether
 * a student is eligible to enroll in a specific course section. Each validator focuses
 * on a specific eligibility criterion, such as prerequisite completion, time conflicts,
 * or section capacity.</p>
 * 
 * <p>The system uses the Chain of Responsibility pattern with these validators to
 * check multiple enrollment criteria sequentially.</p>
 */
public interface EnrollmentValidator {
    
    /**
     * Validates whether a student can enroll in a specific course section
     * based on the criterion this validator is responsible for checking.
     *
     * @param s The student attempting to enroll
     * @param sec The section the student wants to enroll in
     * @return A ValidationResult indicating success (criterion met) or failure (criterion not met)
     */
    ValidationResult validate(Student s, Section sec);
}