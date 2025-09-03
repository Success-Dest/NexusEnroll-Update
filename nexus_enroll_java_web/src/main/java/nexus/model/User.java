package nexus.model;

import java.io.Serializable;

/**
 * Abstract base class representing a user in the Nexus Enrollment system.
 * 
 * <p>The User class contains common attributes and behaviors shared by all types of users
 * in the system. It serves as the parent class for specific user types such as students
 * and instructors.</p>
 * 
 * <p>This class implements {@code Serializable} to support persistence operations.</p>
 */
public abstract class User implements Serializable {
    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    
    /** Unique identifier for the user. Once set, this cannot be changed. */
    protected final String id;
    
    /** The user's full name. */
    protected String name;
    
    /** The user's email address. */
    protected String email;

    /**
     * Protected default constructor for the User class.
     * Creates a user with null ID. This constructor is primarily for use by ORM frameworks.
     */
    protected User() {
        this.id = null;
    }

    /**
     * Creates a new user with the specified identifier, name, and email.
     *
     * @param id The unique identifier for this user
     * @param name The user's full name
     * @param email The user's email address
     */
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return The user's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of this user.
     *
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the email address of this user.
     *
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the name of this user.
     *
     * @param name The new name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the email address of this user.
     *
     * @param email The new email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
