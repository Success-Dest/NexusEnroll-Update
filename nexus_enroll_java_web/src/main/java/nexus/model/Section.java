package nexus.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a specific section of a course in the Nexus Enrollment system.
 * 
 * <p>A section represents a specific offering of a course, with details about meeting times,
 * location, instructor, and enrollment capacity. Sections provide the concrete instances
 * of courses that students can enroll in.</p>
 * 
 * <p>This class implements thread-safe enrollment operations to prevent over-enrollment
 * when multiple students attempt to enroll simultaneously.</p>
 * 
 * <p>This class implements {@code Serializable} to support persistence operations.</p>
 */
public class Section implements Serializable {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Unique identifier for the section. */
    public String sectionId;
    
    /** Identifier of the course this section belongs to. */
    public String courseId;
    
    /** Identifier of the instructor teaching this section. */
    public String instructorId;
    
    /** Maximum number of students allowed to enroll in this section. */
    public int capacity;
    
    /** Current number of students enrolled in this section. Thread-safe counter. */
    private AtomicInteger enrolledCount = new AtomicInteger(0);
    
    /** The day of the week when this section meets. */
    public DayOfWeek day;
    
    /** The start time of the class session. */
    public LocalTime start;
    
    /** The end time of the class session. */
    public LocalTime end;

    /**
     * Default constructor for the Section class.
     * Creates an empty section object with no initialized values.
     */
    public Section() {
    }

    /**
     * Creates a new section with the specified details.
     *
     * @param sectionId The unique identifier for this section
     * @param courseId The identifier of the course this section belongs to
     * @param instructorId The identifier of the instructor teaching this section
     * @param capacity The maximum number of students allowed in this section
     * @param day The day of the week when this section meets
     * @param start The start time of the class session
     * @param end The end time of the class session
     */
    public Section(String sectionId, String courseId, String instructorId, int capacity,
            DayOfWeek day, LocalTime start, LocalTime end) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.capacity = capacity;
        this.enrolledCount = new AtomicInteger(0);
        this.day = day;
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the current number of students enrolled in this section.
     *
     * @return The current enrollment count
     */
    public int getEnrolledCount() {
        return enrolledCount.get();
    }

    /**
     * Checks if the section has reached its enrollment capacity.
     *
     * @return true if the section is at or above capacity, false otherwise
     */
    public boolean isFull() {
        return enrolledCount.get() >= capacity;
    }

    /**
     * Attempts to enroll one more student in this section.
     * This operation is thread-safe to handle concurrent enrollment attempts.
     *
     * @return true if enrollment was successful, false if the section is full
     */
    public boolean tryEnroll() {
        while (true) {
            int cur = enrolledCount.get();
            if (cur >= capacity)
                return false;
            if (enrolledCount.compareAndSet(cur, cur + 1))
                return true;
        }
    }

    /**
     * Decrements the enrollment count when a student drops the section.
     * This operation is thread-safe to handle concurrent drop operations.
     *
     * @return true if the enrollment count was successfully decremented, false if it was already zero
     */
    public boolean dropOne() {
        while (true) {
            int cur = enrolledCount.get();
            if (cur <= 0)
                return false;
            if (enrolledCount.compareAndSet(cur, cur - 1))
                return true;
        }
    }

    /**
     * Sets the enrolled count to a specific value.
     * This method is primarily used for data initialization or recovery.
     *
     * @param val The new enrollment count value
     */
    public void setEnrolledCount(int val) {
        this.enrolledCount = new AtomicInteger(val);
    }

    /**
     * Returns the current enrolled count for serialization purposes.
     *
     * @return The current enrollment count as a plain integer
     */
    public int getEnrolledCountSerialized() {
        return enrolledCount.get();
    }
}
