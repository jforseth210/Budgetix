package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.SiteUser;

/**
 * Interface for objects that can belong to a specific SiteUser
 */
public interface Ownable {
    /**
     * The owner of the object
     */
    SiteUser getOwner();
}
