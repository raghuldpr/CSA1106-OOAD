package edu.simats.ccrs.repository.objectdb;

import edu.simats.ccrs.domain.Student;
import edu.simats.ccrs.repository.DuplicateObjectException;
import edu.simats.ccrs.repository.RepositoryException;
import edu.simats.ccrs.repository.StudentRepository;
import java.util.List;
import java.util.Optional;

public class ObjectDbStudentRepository implements StudentRepository {
    private final ObjectDbContext db;
    public ObjectDbStudentRepository(ObjectDbContext db) { this.db = db; }

    @Override public void create(Student s) {
        if (findById(s.getId()).isPresent()) throw new DuplicateObjectException("duplicate student id " + s.getId());
        db.write(em -> em.persist(s));
    }
    @Override public Optional<Student> findById(String id) {
        return db.read(em -> Optional.ofNullable(em.find(Student.class, id)));
    }
    @Override public List<Student> findAll() {
        return db.read(em -> em.createQuery("SELECT s FROM Student s ORDER BY s.id", Student.class).getResultList());
    }
    @Override public void update(Student s) {
        if (findById(s.getId()).isEmpty()) throw new RepositoryException("student not found: " + s.getId());
        db.write(em -> em.merge(s));
    }
    @Override public void delete(String id) {
        db.write(em -> {
            Student s = em.find(Student.class, id);
            if (s == null) throw new RepositoryException("student not found: " + id);
            long refs = em.createQuery("SELECT COUNT(r) FROM Registration r WHERE r.student.id = :id", Long.class)
                    .setParameter("id", id).getSingleResult();
            if (refs > 0) throw new RepositoryException("student " + id + " has registrations; drop them first");
            em.remove(s);
        });
    }
}
