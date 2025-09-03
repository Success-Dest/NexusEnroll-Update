package nexus.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a student in the Nexus Enrollment system.
 * 
 * <p>The Student class extends the {@link User} class, adding functionality specific to students,
 * such as tracking completed courses. This information is used to validate enrollment eligibility
 * for courses with prerequisites.</p>
 */
public class Student extends User {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Set of course IDs that the student has successfully completed. */
    private final Set<String> completedCourses = new HashSet<>();

    /**
     * Default constructor for the Student class.
     * Creates a student with null values. This constructor is primarily for use by ORM frameworks.
     */
    public Student() {
        super(null, null, null);
    }

    /**
     * Creates a new student with the specified identifier, name, and email.
     *
     * @param id The unique identifier for this student
     * @param name The student's full name
     * @param email The student's email address
     */
    public Student(String id, String name, String email) {
        super(id, name, email);
    }

    /**
     * Adds a course to the student's list of completed courses.
     *
     * @param courseId The identifier of the course that the student has completed
     */
    public void addCompletedCourse(String courseId) {
        completedCourses.add(courseId);
    }

    /**
     * Checks if the student has completed a specific course.
     *
     * @param courseId The identifier of the course to check
     * @return true if the student has completed the course, false otherwise
     */
    public boolean hasCompleted(String courseId) {
        return completedCourses.contains(courseId);
    }

    /**
     * Returns the set of all courses completed by this student.
     *
     * @return An unmodifiable set of course IDs that the student has completed
     */
    public Set<String> getCompletedCourses() {
        return completedCourses;
    }
}
