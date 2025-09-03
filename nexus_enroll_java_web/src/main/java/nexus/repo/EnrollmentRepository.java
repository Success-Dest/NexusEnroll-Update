package nexus.repo;

import nexus.model.Enrollment;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing enrollment data in the Nexus Enrollment system.
 * 
 * <p>This interface defines the operations that can be performed on enrollment records,
 * including saving enrollments, finding enrollments by section or student, and retrieving
 * specific enrollment records by ID.</p>
 * 
 * <p>Implementations of this interface may store enrollment data in various backing stores
 * such as in-memory collections, databases, or external services.</p>
 */
public interface EnrollmentRepository {
    
    /**
     * Saves or updates an enrollment record in the repository.
     * If an enrollment with the same ID already exists, it will be updated.
     *
     * @param e The enrollment object to save or update
     */
    void save(Enrollment e);
    
    /**
     * Finds all enrollment records associated with a specific course section.
     *
     * @param sectionId The unique identifier of the section to find enrollments for
     * @return A list of enrollment records for the specified section
     */
    List<Enrollment> findBySection(String sectionId);
    
    /**
     * Finds all enrollment records associated with a specific student.
     *
     * @param studentId The unique identifier of the student to find enrollments for
     * @return A list of enrollment records for the specified student
     */
    List<Enrollment> findByStudent(String studentId);
    
    /**
     * Finds a specific enrollment record by its unique identifier.
     *
     * @param enrollmentId The unique identifier of the enrollment to find
     * @return An Optional containing the enrollment if found, or an empty Optional if not found
     */
    Optional<Enrollment> find(String enrollmentId);
}
