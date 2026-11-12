package edu.simats.ccrs.repository.memory;

import edu.simats.ccrs.domain.Course;
import edu.simats.ccrs.repository.CourseRepository;
import edu.simats.ccrs.repository.DuplicateObjectException;
import edu.simats.ccrs.repository.RepositoryException;
import java.util.*;

public class InMemoryCourseRepository implements CourseRepository {
    private final Map<String, Course> store = new TreeMap<>();
    @Override public void create(Course c) {
        if (store.putIfAbsent(c.getCode(), c) != null) throw new DuplicateObjectException("duplicate course " + c.getCode());
    }
    @Override public Optional<Course> findByCode(String code) { return Optional.ofNullable(store.get(code)); }
    @Override public List<Course> findAll() { return new ArrayList<>(store.values()); }
    @Override public void update(Course c) { store.put(c.getCode(), c); }
    @Override public void delete(String code) {
        if (store.remove(code) == null) throw new RepositoryException("course not found: " + code);
    }
}
