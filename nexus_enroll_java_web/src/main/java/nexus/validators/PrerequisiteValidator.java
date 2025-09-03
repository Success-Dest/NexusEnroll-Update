package nexus.validators;

import nexus.model.Student;
import nexus.model.Section;
import nexus.model.Course;
import nexus.repo.*;

/**
 * Validator that checks whether a student has completed all prerequisite courses
 * for a course they want to enroll in.
 * 
 * <p>This validator ensures that students have the necessary background knowledge
 * before enrolling in advanced courses by verifying that all prerequisite courses
 * have been completed successfully.</p>
 */
public class PrerequisiteValidator implements EnrollmentValidator {
    /** Repository for accessing course information. */
    private final CourseRepository courseRepo;

    /**
     * Primary constructor - accepts the repository interface.
     * This is the preferred constructor as it depends on the interface rather than a specific implementation.
     *
     * @param repo Repository providing access to course information
     */
    public PrerequisiteValidator(CourseRepository repo) {
        this.courseRepo = repo;
    }

    /**
     * Convenience constructor - accepts the concrete InMemoryCourseRepo implementation.
     *
     * @param repo In-memory implementation of the course repository
     */
    public PrerequisiteValidator(InMemoryCourseRepo repo) {
        this((CourseRepository) repo);
    }

    /**
     * Validates that the student has completed all prerequisites for the course
     * associated with the section they want to enroll in.
     *
     * @param s The student attempting to enroll
     * @param sec The section the student wants to enroll in
     * @return A ValidationResult indicating success (all prerequisites met) or 
     *         failure (one or more prerequisites missing)
     */
    @Override
    public ValidationResult validate(Student s, Section sec) {
        if (sec == null) {
            return ValidationResult.fail("Section not found");
        }

        Course c = courseRepo.findCourse(sec.courseId).orElse(null);
        if (c == null) {
            return ValidationResult.fail("Course not found");
        }

        for (String pre : c.prerequisites) {
            if (!s.hasCompleted(pre))
                return ValidationResult.fail("Missing prerequisite: " + pre);
        }
        
        return ValidationResult.success();
    }
}
