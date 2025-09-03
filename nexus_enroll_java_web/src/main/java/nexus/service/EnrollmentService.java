package nexus.service;

import nexus.model.*;
import nexus.repo.*;
import nexus.validators.*;
import nexus.waitlist.WaitlistManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core service responsible for managing student enrollments in the Nexus Enrollment system.
 * 
 * <p>This service provides operations for students to enroll in and drop course sections.
 * It handles the enrollment workflow including validation, waitlist management, and seat
 * allocation.</p>
 * 
 * <p>The service uses a series of validators to check enrollment eligibility criteria
 * such as prerequisites, time conflicts, and section capacity. If a section is full,
 * students are automatically added to a waitlist and promoted when seats become available.</p>
 * 
 * <p>Thread safety is ensured through synchronized methods to handle concurrent enrollment
 * and drop requests.</p>
 */
public class EnrollmentService {
    /** Repository for accessing student information. */
    private final StudentRepository studentRepo;
    
    /** Repository for accessing course and section information. */
    private final CourseRepository courseRepo;
    
    /** Repository for accessing and storing enrollment records. */
    private final EnrollmentRepository enrollmentRepo;
    
    /** Manager for handling course section waitlists. */
    private final WaitlistManager waitlistManager;
    
    /** List of validators to check enrollment eligibility criteria. */
    private final List<EnrollmentValidator> validators;
    
    /** Generator for unique enrollment IDs. */
    private final AtomicInteger idGen = new AtomicInteger(1);

    /**
     * Primary constructor using repository interfaces.
     * This is the preferred constructor as it depends on interfaces rather than specific implementations.
     *
     * @param sr Repository providing access to student information
     * @param cr Repository providing access to course and section information
     * @param er Repository providing access to enrollment records
     * @param wl Manager for handling course section waitlists
     * @param validators List of validators to check enrollment eligibility criteria
     */
    public EnrollmentService(StudentRepository sr, CourseRepository cr, EnrollmentRepository er,
                             WaitlistManager wl, List<EnrollmentValidator> validators) {
        this.studentRepo = sr;
        this.courseRepo = cr;
        this.enrollmentRepo = er;
        this.waitlistManager = wl;
        this.validators = validators;
    }

    /**
     * Convenience constructor that accepts concrete InMemory* repositories.
     * This prevents 'constructor undefined' errors when callers pass concrete types.
     *
     * @param sr In-memory implementation of the student repository
     * @param cr In-memory implementation of the course repository
     * @param er In-memory implementation of the enrollment repository
     * @param wl Manager for handling course section waitlists
     * @param validators List of validators to check enrollment eligibility criteria
     */
    public EnrollmentService(InMemoryStudentRepo sr, InMemoryCourseRepo cr, InMemoryEnrollmentRepo er,
                             WaitlistManager wl, List<EnrollmentValidator> validators) {
        this((StudentRepository) sr, (CourseRepository) cr, (EnrollmentRepository) er, wl, validators);
    }

    /**
     * Attempts to enroll a student in a course section.
     * 
     * <p>This method checks various enrollment criteria using the configured validators.
     * If all criteria are met and the section has capacity, the student is enrolled.
     * If the section is full or becomes full during processing, the student is placed
     * on a waitlist.</p>
     * 
     * <p>Administrative users can override validation checks by setting adminOverride to true.</p>
     *
     * @param studentId The identifier of the student to enroll
     * @param sectionId The identifier of the section to enroll in
     * @param adminOverride Whether to bypass validation checks (for administrative use)
     * @return A message describing the result of the enrollment attempt
     */
    public synchronized String enroll(String studentId, String sectionId, boolean adminOverride) {
        Optional<Student> sOpt = studentRepo.findById(studentId);
        Optional<Section> secOpt = courseRepo.findSection(sectionId);
        if (!sOpt.isPresent()) return "Student not found";
        if (!secOpt.isPresent()) return "Section not found";
        Student s = sOpt.get(); Section sec = secOpt.get();

        if (!adminOverride) {
            for (EnrollmentValidator v : validators) {
                ValidationResult r = v.validate(s, sec);
                if (!r.ok) {
                    if ("Section is full".equals(r.message)) {
                        // add to waitlist
                        waitlistManager.addToWaitlist(sectionId, studentId);
                        String eid = "E" + idGen.getAndIncrement();
                        Enrollment e = new Enrollment(eid, studentId, sectionId, EnrollmentStatus.WAITLISTED);
                        enrollmentRepo.save(e);
                        return "Added to waitlist: " + eid + " Reason: " + r.message;
                    }
                    return "Enrollment failed: " + r.message;
                }
            }
        }

        // attempt to reserve a seat
        boolean reserved = sec.tryEnroll();
        if (!reserved) {
            waitlistManager.addToWaitlist(sectionId, studentId);
            String eid = "E" + idGen.getAndIncrement();
            Enrollment e = new Enrollment(eid, studentId, sectionId, EnrollmentStatus.WAITLISTED);
            enrollmentRepo.save(e);
            return "Added to waitlist (race): " + eid;
        }

        String eid = "E" + idGen.getAndIncrement();
        Enrollment e = new Enrollment(eid, studentId, sectionId, EnrollmentStatus.ENROLLED);
        enrollmentRepo.save(e);
        return "Enrolled: " + eid;
    }

    /**
     * Drops a student from a course section they are currently enrolled in.
     * 
     * <p>This method updates the enrollment status to DROPPED, frees up a seat in the section,
     * and promotes the next student from the waitlist if any are waiting.</p>
     *
     * @param studentId The identifier of the student dropping the course
     * @param sectionId The identifier of the section to drop
     * @return A message describing the result of the drop operation
     */
    public synchronized String drop(String studentId, String sectionId) {
        List<Enrollment> list = enrollmentRepo.findByStudent(studentId);
        Optional<Enrollment> found = list.stream().filter(en -> en.sectionId.equals(sectionId) && en.status == EnrollmentStatus.ENROLLED).findFirst();
        if (!found.isPresent()) return "No enrollment found to drop";
        Enrollment en = found.get();
        en.status = EnrollmentStatus.DROPPED;
        courseRepo.findSection(sectionId).ifPresent(sec -> sec.dropOne());
        Optional<String> next = waitlistManager.popNext(sectionId);
        if (next.isPresent()) {
            String nextStudent = next.get();
            // promote: attempt to enroll the student (adminOverride true to bypass prereq checks when promoting)
            String res = enroll(nextStudent, sectionId, true);
            return "Dropped. Promoted: " + res;
        }
        return "Dropped. No waitlist promotions.";
    }
}
