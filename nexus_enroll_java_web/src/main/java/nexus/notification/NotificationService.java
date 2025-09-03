package nexus.notification;

import nexus.waitlist.WaitlistManager;

/**
 * Service responsible for sending notifications to students in the Nexus Enrollment system.
 * 
 * <p>This class implements the WaitlistObserver interface to receive callbacks when
 * seats become available in course sections. When notified about seat availability,
 * it sends appropriate notifications to students on the waitlist.</p>
 * 
 * <p>In the current implementation, notifications are printed to the console as a
 * demonstration, but this could be extended to use email, SMS, or other notification
 * channels in a production environment.</p>
 */
public class NotificationService implements WaitlistManager.WaitlistObserver {
    
    /**
     * Handles the event when a seat becomes available in a course section.
     * Sends a notification to the specified student informing them about
     * the seat availability.
     *
     * @param sectionId The identifier of the section where a seat has become available
     * @param studentId The identifier of the student who is next on the waitlist
     */
    @Override
    public void onSeatAvailable(String sectionId, String studentId) {
        System.out.printf("[Notification] Student %s: seat available in section %s\n", studentId, sectionId);
    }
}
