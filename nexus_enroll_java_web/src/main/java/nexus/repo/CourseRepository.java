package nexus.repo;

import nexus.model.Course;
import nexus.model.Section;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing course and section data in the Nexus Enrollment system.
 * 
 * <p>This interface defines the operations that can be performed on course and section records,
 * including finding courses and sections by ID, saving course and section information, and
 * listing sections for a particular course.</p>
 * 
 * <p>Implementations of this interface may store course and section data in various backing stores
 * such as in-memory collections, databases, or external services.</p>
 */
public interface CourseRepository {
    
    /**
     * Finds a course by its unique identifier.
     *
     * @param id The unique identifier of the course to find
     * @return An Optional containing the course if found, or an empty Optional if not found
     */
    Optional<Course> findCourse(String id);
    
    /**
     * Finds a section by its unique identifier.
     *
     * @param sectionId The unique identifier of the section to find
     * @return An Optional containing the section if found, or an empty Optional if not found
     */
    Optional<Section> findSection(String sectionId);
    
    /**
     * Saves or updates a course record in the repository.
     * If a course with the same ID already exists, it will be updated.
     *
     * @param c The course object to save or update
     */
    void saveCourse(Course c);
    
    /**
     * Saves or updates a section record in the repository.
     * If a section with the same ID already exists, it will be updated.
     *
     * @param s The section object to save or update
     */
    void saveSection(Section s);
    
    /**
     * Lists all sections available for a specific course.
     *
     * @param courseId The unique identifier of the course to find sections for
     * @return A list of sections for the specified course
     */
    List<Section> listSectionsForCourse(String courseId);

    /**
     * Helper: return all courses in the repository (used by the web UI).
     * 
     * @return A list containing all courses currently in the repository
     */
    List<Course> findCourseList();
}
