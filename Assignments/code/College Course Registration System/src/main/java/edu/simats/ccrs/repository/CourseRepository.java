package edu.simats.ccrs.repository;

import edu.simats.ccrs.domain.Course;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    void create(Course c);
    Optional<Course> findByCode(String code);
    List<Course> findAll();
    void update(Course c);
    void delete(String code);
}
