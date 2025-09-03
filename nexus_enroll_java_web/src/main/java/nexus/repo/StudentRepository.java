package nexus.repo;

import nexus.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing student data in the Nexus Enrollment system.
 * 
 * <p>This interface defines the operations that can be performed on student records,
 * including retrieval by ID, saving student information, and listing all student IDs.</p>
 * 
 * <p>Implementations of this interface may store student data in various backing stores
 * such as in-memory collections, databases, or external services.</p>
 */
public interface StudentRepository {
    
    /**
     * Finds a student by their unique identifier.
     *
     * @param id The unique identifier of the student to find
     * @return An Optional containing the student if found, or an empty Optional if not found
     */
    Optional<Student> findById(String id);
    
    /**
     * Saves or updates a student record in the repository.
     * If a student with the same ID already exists, it will be updated.
     *
     * @param s The student object to save or update
     */
    void save(Student s);

    /**
     * Helper: return a list of stored student IDs.
     * 
     * @return A list containing all student IDs currently in the repository
     */
    List<String> listIds();
}
