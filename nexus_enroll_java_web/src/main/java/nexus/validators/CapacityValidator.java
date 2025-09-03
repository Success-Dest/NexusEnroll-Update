package nexus.validators;

import nexus.model.Student;
import nexus.model.Section;

/**
 * Validator that checks whether a course section has available capacity for enrollment.
 * 
 * <p>This validator ensures that students cannot enroll in sections that have already
 * reached their maximum capacity. If a section is full, enrollment requests are
 * typically redirected to a waitlist.</p>
 */
public class CapacityValidator implements EnrollmentValidator {
    
    /**
     * Validates that the section the student wants to enroll in has available capacity.
     *
     * @param s The student attempting to enroll
     * @param sec The section the student wants to enroll in
     * @return A ValidationResult indicating success (capacity available) or failure (section is full)
     */
    @Override
    public ValidationResult validate(Student s, Section sec) {
        if (sec == null) {
            return ValidationResult.fail("Section not found");
        }

        if (sec.isFull()) {
            return ValidationResult.fail("Section is full");
        }
        
        return ValidationResult.success();
    }
}
