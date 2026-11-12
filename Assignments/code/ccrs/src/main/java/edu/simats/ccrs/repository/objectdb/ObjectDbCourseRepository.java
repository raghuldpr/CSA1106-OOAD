package edu.simats.ccrs.repository.objectdb;

import edu.simats.ccrs.domain.Course;
import edu.simats.ccrs.repository.CourseRepository;
import edu.simats.ccrs.repository.DuplicateObjectException;
import edu.simats.ccrs.repository.RepositoryException;
import java.util.List;
import java.util.Optional;

public class ObjectDbCourseRepository implements CourseRepository {
    private final ObjectDbContext db;
    public ObjectDbCourseRepository(ObjectDbContext db) { this.db = db; }

    @Override public void create(Course c) {
        if (findByCode(c.getCode()).isPresent()) throw new DuplicateObjectException("duplicate course " + c.getCode());
        db.write(em -> {
            if (c.getInstructor() != null && em.find(c.getInstructor().getClass(), c.getInstructor().getId()) == null)
                em.persist(c.getInstructor());
            em.persist(c);
        });
    }
    @Override public Optional<Course> findByCode(String code) {
        return db.read(em -> Optional.ofNullable(em.find(Course.class, code)));
    }
    @Override public List<Course> findAll() {
        return db.read(em -> em.createQuery("SELECT c FROM Course c ORDER BY c.code", Course.class).getResultList());
    }
    @Override public void update(Course c) { db.write(em -> em.merge(c)); }
    @Override public void delete(String code) {
        db.write(em -> {
            Course c = em.find(Course.class, code);
            if (c == null) throw new RepositoryException("course not found: " + code);
            em.remove(c);
        });
    }
}
