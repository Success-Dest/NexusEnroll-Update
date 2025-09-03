package nexus.model;
import java.io.Serializable;

/**
 * Represents an enrollment record in the Nexus Enrollment system.
 * This class stores information about a student's enrollment in a specific course section.
 * 
 * <p>Each enrollment has a unique identifier, references to the student and section involved,
 * and a status indicating the current state of the enrollment.</p>
 * 
 * <p>This class implements {@code Serializable} to support persistence operations.</p>
 */
public class Enrollment implements Serializable {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Unique identifier for this enrollment record. */
    public String enrollmentId;
    
    /** Identifier of the student associated with this enrollment. */
    public String studentId;
    
    /** Identifier of the course section in which the student is enrolled. */
    public String sectionId;
    
    /** Current status of this enrollment (e.g., PENDING, APPROVED, REJECTED). */
    public EnrollmentStatus status;
    
    /**
     * Default constructor for the Enrollment class.
     * Creates an empty enrollment object with no initialized values.
     */
    public Enrollment(){}

    /**
     * Creates a new enrollment with specified details.
     *
     * @param enrollmentId Unique identifier for this enrollment
     * @param studentId Identifier of the student enrolling in the course
     * @param sectionId Identifier of the course section being enrolled in
     * @param status Current status of the enrollment
     */
    public Enrollment(String enrollmentId, String studentId, String sectionId, EnrollmentStatus status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
    }
}
