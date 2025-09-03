package nexus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an academic course in the Nexus Enrollment system.
 * 
 * <p>The Course class contains basic information about a course including its identifier,
 * name, and any prerequisite courses required before a student can enroll in this course.</p>
 * 
 * <p>This class implements {@code Serializable} to support persistence operations.</p>
 */
public class Course implements Serializable {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Unique identifier for the course, typically a department code and number (e.g., "CS101"). */
    public String courseId;
    
    /** The full name or title of the course. */
    public String name;
    
    /** List of course IDs that are prerequisites for this course. */
    public final List<String> prerequisites = new ArrayList<>();

    /**
     * Default constructor for the Course class.
     * Creates an empty course object with no initialized values.
     */
    public Course() {
    }

    /**
     * Creates a new course with the specified identifier and name.
     *
     * @param courseId The unique identifier for this course
     * @param name The full name or title of the course
     */
    public Course(String courseId, String name) {
        this.courseId = courseId;
        this.name = name;
    }
}
