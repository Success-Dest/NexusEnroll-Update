package nexus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Spark;
import static spark.Spark.*;
import nexus.model.*;
import nexus.repo.*;
import nexus.waitlist.WaitlistManager;
import nexus.notification.NotificationService;
import nexus.validators.*;
import nexus.service.*;
import java.io.File;
import java.util.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Main web application class for the Nexus Enrollment system.
 * 
 * <p>This class serves as the entry point for the Nexus Enrollment web application.
 * It initializes the repositories, services, and validators needed by the system,
 * loads persistent data from files, and sets up the RESTful API endpoints using
 * the Spark web framework.</p>
 * 
 * <p>The application provides endpoints for student management, course catalog access,
 * enrollment operations, and system initialization. All data is persisted to disk
 * to ensure it survives application restarts.</p>
 * 
 * <p>The web server runs on port 4567 and serves static files from the resources/public
 * directory for the front-end user interface.</p>
 */
public class WebMain {
    /** Directory where persistent data files are stored. */
    static final String DATA_DIR = "data";
    
    /** File path for storing student repository data. */
    static final String STUD_FILE = DATA_DIR + "/students.bin";
    
    /** File path for storing course and section repository data. */
    static final String COURSE_FILE = DATA_DIR + "/courses.bin";
    
    /** File path for storing enrollment repository data. */
    static final String ENR_FILE = DATA_DIR + "/enrollments.bin";

    /**
     * Main entry point for the Nexus Enrollment web application.
     * 
     * <p>Initializes all required components, loads persistent data, sets up API endpoints,
     * and starts the web server.</p>
     *
     * @param args Command-line arguments (not used)
     * @throws Exception If an error occurs during initialization or server startup
     */
    public static void main(String[] args) throws Exception {
        // Create the data directory if it doesn't exist
        new File(DATA_DIR).mkdirs();
        
        // Initialize repositories
        InMemoryStudentRepo sRepo = new InMemoryStudentRepo();
        InMemoryCourseRepo cRepo = new InMemoryCourseRepo();
        InMemoryEnrollmentRepo eRepo = new InMemoryEnrollmentRepo();

        // Load persisted data from files
        try {
            sRepo.loadFromFile(STUD_FILE);
        } catch (Exception ex) {
            System.out.println("No student data: " + ex.getMessage());
        }

        try {
            cRepo.loadFromFile(COURSE_FILE);
        } catch (Exception ex) {
            System.out.println("No course data: " + ex.getMessage());
        }
        
        try {
            eRepo.loadFromFile(ENR_FILE);
        } catch (Exception ex) {
            System.out.println("No enrollment data: " + ex.getMessage());
        }

        // Initialize waitlist and notification components
        WaitlistManager wl = new WaitlistManager();
        NotificationService notifier = new NotificationService();
        wl.addObserver(notifier);
        
        // Set up validators for enrollment rules
        List<EnrollmentValidator> validators = Arrays.asList(
            new PrerequisiteValidator(cRepo),
            new CapacityValidator(),
            new TimeConflictValidator(eRepo)
        );
        
        // Create application services
        EnrollmentService enrollSvc = new EnrollmentService(sRepo, cRepo, eRepo, wl, validators);
        GradeService gradeSvc = new GradeService();

        // Initialize sample data if the repository is empty
        if (cRepo.findCourse("CS102").isEmpty()) {
            initSampleData(sRepo, cRepo);
            sRepo.saveToFile(STUD_FILE);
            cRepo.saveToFile(COURSE_FILE);
        }

        // Configure web server
        staticFiles.location("/public"); // resources/public
        port(4567);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // API Endpoint: List all students with details
        get("/api/students", (req, res) -> {
            res.type("application/json");
            List<Map<String,Object>> list = new ArrayList<>();
            for (String id : sRepo.listIds()) {
                sRepo.findById(id).ifPresent(st -> {
                    Map<String,Object> m = new HashMap<>();
                    m.put("id", st.getId());
                    m.put("name", st.getName());
                    m.put("email", st.getEmail());
                    m.put("completedCourses", st.getCompletedCourses());
                    list.add(m);
                });
            }
            return gson.toJson(list);
        });

        // API Endpoint: Create a new student
        post("/api/students", (req, res) -> {
            res.type("application/json");
            Map body = gson.fromJson(req.body(), Map.class);
            String id = (String) body.get("id");
            String name = (String) body.get("name");
            String email = (String) body.get("email");

            if (name == null || name.trim().isEmpty()) {
                res.status(400);
                return gson.toJson(Collections.singletonMap("error", "name required"));
            }
            if (id == null || id.trim().isEmpty()) {
                // generate a basic id if not provided
                id = "S" + (int)(Math.random() * 100000);
            }
            Student st = new Student(id, name, email == null ? "" : email);
            sRepo.save(st);

            // persist students to disk
            try { sRepo.saveToFile(STUD_FILE); } catch (Exception ex) { System.out.println("persist student err:" + ex.getMessage()); }

            return gson.toJson(Collections.singletonMap("id", id));
        });

        // API Endpoint: Get course catalog with sections
        get("/api/catalog", (req, res) -> {
            res.type("application/json");

            Map<String,Object> out = new HashMap<>();
            List<Map<String,Object>> courses = new ArrayList<>();

            // build simple DTOs
            for (Course c : cRepo.findCourseList()) {
                Map<String,Object> m = new HashMap<>();
                m.put("courseId", c.courseId);
                m.put("name", c.name);
                m.put("prerequisites", c.prerequisites);

                List<Map<String,Object>> sectionDtos = new ArrayList<>();
                List<Section> sections = cRepo.listSectionsForCourse(c.courseId);
                for (Section s : sections) {
                    Map<String,Object> sd = new HashMap<>();
                    sd.put("sectionId", s.sectionId);
                    sd.put("courseId", s.courseId);
                    sd.put("instructorId", s.instructorId);
                    sd.put("capacity", s.capacity);
                    // convert day/time to simple strings (avoid direct LocalTime/DayOfWeek serialization)
                    sd.put("day", s.day == null ? null : s.day.toString());
                    sd.put("start", s.start == null ? null : s.start.toString());
                    sd.put("end", s.end == null ? null : s.end.toString());
                    // atomic integer -> int
                    sd.put("enrolledCount", s.getEnrolledCount());
                    sd.put("isFull", s.isFull());

                    sectionDtos.add(sd);
                }

                m.put("sections", sectionDtos);
                courses.add(m);
            }

            out.put("courses", courses);
            return gson.toJson(out);
        });

        // API Endpoint: List all students (alternative endpoint)
        get("/api/students", (req, res) -> {
            res.type("application/json");
            List<Student> list = new ArrayList<>();
            for (String id : sRepo.listIds())
                sRepo.findById(id).ifPresent(list::add);
            return gson.toJson(list);
        });

        // API Endpoint: Enroll a student in a section
        post("/api/enroll", (req, res) -> {
            res.type("application/json");
            Map body = gson.fromJson(req.body(), Map.class);
            String sid = (String) body.get("studentId");
            String sectionId = (String) body.get("sectionId");
            boolean admin = Boolean.TRUE.equals(body.get("admin"));
            String r = enrollSvc.enroll(sid, sectionId, admin);
            try {
                eRepo.saveToFile(ENR_FILE);
                cRepo.saveToFile(COURSE_FILE);
            } catch (Exception ex) {
                System.out.println("persist err:" + ex.getMessage());
            }
            return gson.toJson(Collections.singletonMap("result", r));
        });

        // API Endpoint: Drop a student from a section
        post("/api/drop", (req, res) -> {
            res.type("application/json");
            Map body = gson.fromJson(req.body(), Map.class);
            String sid = (String) body.get("studentId");
            String sectionId = (String) body.get("sectionId");
            String r = enrollSvc.drop(sid, sectionId);
            try {
                eRepo.saveToFile(ENR_FILE);
                cRepo.saveToFile(COURSE_FILE);
            } catch (Exception ex) {
                System.out.println("persist err:" + ex.getMessage());
            }
            return gson.toJson(Collections.singletonMap("result", r));
        });

        // API Endpoint: List all enrollments
        get("/api/enrollments", (req, res) -> {
            res.type("application/json");
            return gson.toJson(eRepo.findAll());
        });
        
        // API Endpoint: Initialize sample data
        post("/api/init", (req, res) -> {
            initSampleData(sRepo, cRepo);
            sRepo.saveToFile(STUD_FILE);
            cRepo.saveToFile(COURSE_FILE);
            return gson.toJson(Collections.singletonMap("status", "ok"));
        });
        
        // Redirect root to index.html
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });
        
        System.out.println("Web server started at http://localhost:4567");
    }

    /**
     * Initializes the system with sample data for testing and demonstration purposes.
     * 
     * <p>Creates two students (Alice and Bob), two courses (Intro CS and Data Structures),
     * and two course sections. Sets up a prerequisite relationship where CS102 requires
     * CS101, and marks Bob as having completed CS101 to demonstrate the prerequisite
     * validation.</p>
     *
     * @param sRepo Student repository to populate with sample students
     * @param cRepo Course repository to populate with sample courses and sections
     */
    static void initSampleData(InMemoryStudentRepo sRepo, InMemoryCourseRepo cRepo) {
        // Create sample students
        Student s1 = new Student("S1", "Alice", "alice@example.com");
        Student s2 = new Student("S2", "Bob", "bob@example.com");
        s2.addCompletedCourse("CS101");
        sRepo.save(s1);
        sRepo.save(s2);
        
        // Create sample courses with prerequisites
        Course cs101 = new Course("CS101", "Intro CS");
        cRepo.saveCourse(cs101);
        Course cs102 = new Course("CS102", "Data Structures");
        cs102.prerequisites.add("CS101");
        cRepo.saveCourse(cs102);
        
        // Create sample course sections
        Section sec1 = new Section("SEC1", "CS101", "F1", 2, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));
        Section sec2 = new Section("SEC2", "CS102", "F2", 1, DayOfWeek.TUESDAY, LocalTime.of(11, 0),
                LocalTime.of(12, 0));
        cRepo.saveSection(sec1);
        cRepo.saveSection(sec2);
    }
}
