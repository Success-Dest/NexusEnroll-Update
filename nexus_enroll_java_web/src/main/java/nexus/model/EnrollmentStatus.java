package nexus.model;

/**
 * Represents the possible states of a student's enrollment in a course section.
 * 
 * <p>The enrollment status determines the student's relationship to a particular course section,
 * whether they are actively enrolled, on a waiting list, or have dropped the course.</p>
 */
public enum EnrollmentStatus {
    /**
     * The student is actively enrolled in the course section.
     */
    ENROLLED, 
    
    /**
     * The student is on a waiting list for the course section.
     */
    WAITLISTED, 
    
    /**
     * The student was previously enrolled but has dropped the course section.
     */
    DROPPED
}
