package nexus.repo;

import nexus.model.Course;
import nexus.model.Section;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the CourseRepository interface.
 * 
 * <p>This class stores course and section data in separate in-memory maps for quick access and
 * provides methods to save and load the data from files for persistence between
 * application restarts.</p>
 * 
 * <p>This implementation is suitable for development, testing, or small-scale
 * deployments where the number of courses and sections is limited.</p>
 */
public class InMemoryCourseRepo implements CourseRepository, Serializable {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Map that stores course objects, keyed by their unique identifier. */
    private final Map<String, Course> courses = new HashMap<>();
    
    /** Map that stores section objects, keyed by their unique identifier. */
    private final Map<String, Section> sections = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Course> findCourse(String id) { return Optional.ofNullable(courses.get(id)); }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Section> findSection(String sectionId) { return Optional.ofNullable(sections.get(sectionId)); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCourse(Course c) { courses.put(c.courseId, c); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSection(Section s) { sections.put(s.sectionId, s); }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Section> listSectionsForCourse(String courseId) {
        return sections.values().stream().filter(s -> s.courseId.equals(courseId)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Course> findCourseList() { return new ArrayList<>(courses.values()); }

    /**
     * Saves all course and section data to a file at the specified path.
     * This enables persistence of course and section data between application restarts.
     *
     * @param path The file path where the data should be saved
     * @throws IOException If an I/O error occurs during the save operation
     */
    public void saveToFile(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(courses);
            oos.writeObject(sections);
        }
    }

    /**
     * Loads course and section data from a file at the specified path.
     * If the file doesn't exist, no data is loaded and the repository remains empty.
     *
     * @param path The file path from which to load course and section data
     * @throws IOException If an I/O error occurs during the load operation
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile(String path) throws IOException, ClassNotFoundException {
        File f = new File(path);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object a = ois.readObject();
            Object b = ois.readObject();
            if (a instanceof Map) {
                courses.clear();
                courses.putAll((Map<String, Course>) a);
            }
            if (b instanceof Map) {
                sections.clear();
                sections.putAll((Map<String, Section>) b);
            }
        }
    }
}
