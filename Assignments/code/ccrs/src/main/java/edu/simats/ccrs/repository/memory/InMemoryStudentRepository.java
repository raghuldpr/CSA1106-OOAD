package edu.simats.ccrs.repository.memory;

import edu.simats.ccrs.domain.Student;
import edu.simats.ccrs.repository.DuplicateObjectException;
import edu.simats.ccrs.repository.RepositoryException;
import edu.simats.ccrs.repository.StudentRepository;
import java.util.*;

/** Alternative persistence mechanism used to prove persistence independence. */
public class InMemoryStudentRepository implements StudentRepository {
    private final Map<String, Student> store = new TreeMap<>();
    @Override public void create(Student s) {
        if (store.putIfAbsent(s.getId(), s) != null) throw new DuplicateObjectException("duplicate student id " + s.getId());
    }
    @Override public Optional<Student> findById(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<Student> findAll() { return new ArrayList<>(store.values()); }
    @Override public void update(Student s) {
        if (!store.containsKey(s.getId())) throw new RepositoryException("student not found: " + s.getId());
        store.put(s.getId(), s);
    }
    @Override public void delete(String id) {
        if (store.remove(id) == null) throw new RepositoryException("student not found: " + id);
    }
}
