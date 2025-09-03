package nexus.waitlist;

import java.util.*;

/**
 * Manages waitlists for course sections in the Nexus Enrollment system.
 * 
 * <p>This class implements the Observer design pattern to notify interested parties
 * when a seat becomes available in a previously full course section. When a student
 * drops a course and a seat becomes available, the WaitlistManager notifies registered
 * observers so they can take appropriate action (e.g., notify the next student on the waitlist).</p>
 * 
 * <p>Each course section can have its own waitlist, and students are managed in
 * first-come-first-served order using a queue data structure.</p>
 */
public class WaitlistManager {
    /** Map of section IDs to their corresponding waitlist queues. */
    private final Map<String, Deque<String>> waitlists = new HashMap<>();
    
    /** List of observers to be notified when a seat becomes available. */
    private final List<WaitlistObserver> observers = new ArrayList<>();

    /**
     * Interface for observers that want to be notified when a seat becomes available
     * in a previously full course section.
     */
    public interface WaitlistObserver {
        /**
         * Called when a seat becomes available in a course section.
         *
         * @param sectionId The identifier of the section where a seat has become available
         * @param studentId The identifier of the student who is next on the waitlist
         */
        void onSeatAvailable(String sectionId, String studentId);
    }

    /**
     * Registers a new observer to be notified of waitlist events.
     *
     * @param o The observer to register
     */
    public void addObserver(WaitlistObserver o) {
        observers.add(o);
    }

    /**
     * Removes an observer from the notification list.
     *
     * @param o The observer to remove
     */
    public void removeObserver(WaitlistObserver o) {
        observers.remove(o);
    }

    /**
     * Adds a student to the waitlist for a specific course section.
     * If the waitlist doesn't exist yet, it will be created.
     *
     * @param sectionId The identifier of the section for which to add to the waitlist
     * @param studentId The identifier of the student to add to the waitlist
     */
    public void addToWaitlist(String sectionId, String studentId) {
        waitlists.computeIfAbsent(sectionId, k -> new ArrayDeque<>()).addLast(studentId);
    }

    /**
     * Retrieves and removes the next student from the waitlist for a specific section.
     * This method also notifies all registered observers that a seat is available
     * for the retrieved student.
     *
     * @param sectionId The identifier of the section for which to get the next waitlisted student
     * @return An Optional containing the student ID if a student was on the waitlist,
     *         or an empty Optional if the waitlist is empty or doesn't exist
     */
    public Optional<String> popNext(String sectionId) {
        Deque<String> q = waitlists.get(sectionId);

        if (q == null || q.isEmpty()) {
            return Optional.empty();
        }

        String studentId = q.removeFirst();

        for (WaitlistObserver o : observers) {
            o.onSeatAvailable(sectionId, studentId);
        }
        
        return Optional.of(studentId);
    }
}
