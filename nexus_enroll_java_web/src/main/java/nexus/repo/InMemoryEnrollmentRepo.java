package nexus.repo;

import nexus.model.Enrollment;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the EnrollmentRepository interface.
 * 
 * <p>This class stores enrollment data in an in-memory map for quick access and
 * provides methods to save and load the data from files for persistence between
 * application restarts.</p>
 * 
 * <p>This implementation is suitable for development, testing, or small-scale
 * deployments where the number of enrollments is limited.</p>
 */
public class InMemoryEnrollmentRepo implements EnrollmentRepository, Serializable {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Map that stores enrollment objects, keyed by their unique identifier. */
    private final Map<String, Enrollment> map = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Enrollment e) { map.put(e.enrollmentId, e); }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Enrollment> findBySection(String sectionId) {
        return map.values().stream().filter(e -> e.sectionId.equals(sectionId)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Enrollment> findByStudent(String studentId) {
        return map.values().stream().filter(e -> e.studentId.equals(studentId)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Enrollment> find(String enrollmentId) { return Optional.ofNullable(map.get(enrollmentId)); }

    /**
     * Returns all enrollment records in the repository.
     * 
     * @return A list containing all enrollment records
     */
    public List<Enrollment> findAll() { return new ArrayList<>(map.values()); }

    /**
     * Saves all enrollment data to a file at the specified path.
     * This enables persistence of enrollment data between application restarts.
     *
     * @param path The file path where the data should be saved
     * @throws IOException If an I/O error occurs during the save operation
     */
    public void saveToFile(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(map);
        }
    }

    /**
     * Loads enrollment data from a file at the specified path.
     * If the file doesn't exist, no data is loaded and the repository remains empty.
     *
     * @param path The file path from which to load enrollment data
     * @throws IOException If an I/O error occurs during the load operation
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile(String path) throws IOException, ClassNotFoundException {
        File f = new File(path);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                map.clear();
                map.putAll((Map<String, Enrollment>) obj);
            }
        }
    }
}
