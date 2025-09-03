package nexus.repo;

import nexus.model.Student;

import java.io.*;
import java.util.*;

/**
 * In-memory implementation of the StudentRepository interface.
 * 
 * <p>This class stores student data in an in-memory map for quick access and
 * provides methods to save and load the data from files for persistence between
 * application restarts.</p>
 * 
 * <p>This implementation is suitable for development, testing, or small-scale
 * deployments where the number of students is limited.</p>
 */
public class InMemoryStudentRepo implements StudentRepository, Serializable {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Map that stores student objects, keyed by their unique identifier. */
    private final Map<String, Student> map = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Student> findById(String id) { return Optional.ofNullable(map.get(id)); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Student s) { map.put(s.getId(), s); }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listIds() { return new ArrayList<>(map.keySet()); }

    /**
     * Saves all student data to a file at the specified path.
     * This enables persistence of student data between application restarts.
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
     * Loads student data from a file at the specified path.
     * If the file doesn't exist, no data is loaded and the repository remains empty.
     *
     * @param path The file path from which to load student data
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
                map.putAll((Map<String, Student>) obj);
            }
        }
    }
}
