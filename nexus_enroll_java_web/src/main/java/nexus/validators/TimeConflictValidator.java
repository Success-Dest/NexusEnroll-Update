package nexus.validators;

import nexus.model.Student;
import nexus.model.Section;
import nexus.repo.EnrollmentRepository;

/**
 * Validator that checks for time conflicts between a section a student wants to enroll in
 * and other sections the student is already enrolled in.
 * 
 * <p>This validator ensures that a student cannot enroll in multiple course sections
 * that have overlapping meeting times. It helps prevent scheduling conflicts for students.</p>
 * 
 * <p>Note: The current implementation is a placeholder that always returns success.
 * In a complete implementation, it would check the student's existing enrollments and
 * verify that there are no time conflicts with the new section.</p>
 */
public class TimeConflictValidator implements EnrollmentValidator {
    /** Repository for accessing enrollment records. */
    private final EnrollmentRepository enrRepo;

    /**
     * Creates a new TimeConflictValidator with the specified enrollment repository.
     *
     * @param enrRepo Repository providing access to enrollment records
     */
    public TimeConflictValidator(EnrollmentRepository enrRepo) {
        this.enrRepo = enrRepo;
    }

    /**
     * Validates that the section a student wants to enroll in does not conflict
     * with any other sections the student is already enrolled in.
     *
     * @param s The student attempting to enroll
     * @param sec The section the student wants to enroll in
     * @return A ValidationResult indicating success (no conflicts) or failure (conflict found)
     */
    @Override
    public ValidationResult validate(Student s, Section sec) {
        // Current implementation is a placeholder - always returns success
        // A complete implementation would check for time conflicts with existing enrollments
        return ValidationResult.success();
    }
}
