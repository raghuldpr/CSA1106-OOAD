package edu.simats.ccrs.repository;

import edu.simats.ccrs.domain.Registration;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository {
    /** Saves the registration and the changed course seat count in ONE transaction. */
    void create(Registration r);
    Optional<Registration> findById(String id);
    List<Registration> findActiveByStudent(String studentId);   // relationship retrieval
    void update(Registration r);
    void delete(String id);
    String nextId();
}
