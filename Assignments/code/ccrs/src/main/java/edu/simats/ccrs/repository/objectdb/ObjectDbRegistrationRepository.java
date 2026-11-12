package edu.simats.ccrs.repository.objectdb;

import edu.simats.ccrs.domain.Registration;
import edu.simats.ccrs.domain.RegistrationStatus;
import edu.simats.ccrs.repository.DuplicateObjectException;
import edu.simats.ccrs.repository.RegistrationIds;
import edu.simats.ccrs.repository.RegistrationRepository;
import edu.simats.ccrs.repository.RepositoryException;
import java.util.List;
import java.util.Optional;

public class ObjectDbRegistrationRepository implements RegistrationRepository {
    private final ObjectDbContext db;
    public ObjectDbRegistrationRepository(ObjectDbContext db) { this.db = db; }

    @Override public void create(Registration r) {
        db.write(em -> {
            if (em.find(Registration.class, r.getId()) != null) throw new DuplicateObjectException("duplicate registration " + r.getId());
            if (em.find(r.getStudent().getClass(), r.getStudent().getId()) == null)
                throw new RepositoryException("invalid relationship: unknown student " + r.getStudent().getId());
            em.merge(r.getCourse());     // seat count changed by the domain
            em.merge(r);                 // object references to Student and Course are stored as OIDs
        });
    }
    @Override public Optional<Registration> findById(String id) {
        return db.read(em -> Optional.ofNullable(em.find(Registration.class, id)));
    }
    @Override public List<Registration> findActiveByStudent(String studentId) {
        return db.read(em -> em.createQuery(
                "SELECT r FROM Registration r WHERE r.student.id = :sid AND r.status = :st ORDER BY r.id", Registration.class)
                .setParameter("sid", studentId).setParameter("st", RegistrationStatus.ACTIVE).getResultList());
    }
    @Override public void update(Registration r) {
        db.write(em -> { em.merge(r.getCourse()); em.merge(r); });
    }
    @Override public void delete(String id) {
        db.write(em -> {
            Registration r = em.find(Registration.class, id);
            if (r == null) throw new RepositoryException("registration not found: " + id);
            em.remove(r);
        });
    }
    @Override public String nextId() {
        String last = db.read(em -> em.createQuery("SELECT MAX(r.id) FROM Registration r", String.class).getSingleResult());
        return RegistrationIds.after(last);
    }
}
