package nexus.service;
import java.util.*;

/**
 * Service responsible for managing student grades in the Nexus Enrollment system.
 * 
 * <p>This service allows instructors to submit grades for students in their courses
 * and provides access to the stored grade information. It ensures that only valid
 * letter grades (A, B, C, D, F) are accepted and stored.</p>
 * 
 * <p>The key format in the grades map is typically a combination of student ID and 
 * course/section ID, allowing for efficient lookup of a student's grade in a particular course.</p>
 */
public class GradeService {
    /** Map storing grades, with keys representing student-course combinations and values representing letter grades. */
    private final Map<String, String> grades = new HashMap<>();
    
    /**
     * Submits a batch of grades for processing.
     * Only valid grades (A, B, C, D, F) will be stored; invalid grades are logged and skipped.
     *
     * @param batch Map of student-course combinations to their corresponding grades
     */
    public void submitGradesBatch(Map<String,String> batch) {
        for (Map.Entry<String,String> e : batch.entrySet()) {
            String key = e.getKey(); String grade = e.getValue();
            if (!Arrays.asList("A","B","C","D","F").contains(grade)) {
                System.out.println("Invalid grade for " + key + ": " + grade + " -> skipping");
                continue;
            }
            grades.put(key, grade);
        }
        System.out.println("Grades processed. Count=" + grades.size());
    }
    
    /**
     * Returns all stored grades in the system.
     *
     * @return A map of student-course combinations to their corresponding grades
     */
    public Map<String,String> getGrades(){ return grades; }
}
