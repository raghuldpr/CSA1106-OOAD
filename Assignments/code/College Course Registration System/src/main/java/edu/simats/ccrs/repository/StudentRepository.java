package edu.simats.ccrs.repository;

import edu.simats.ccrs.domain.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    void create(Student s);              // fails if the id already exists
    Optional<Student> findById(String id);
    List<Student> findAll();
    void update(Student s);
    void delete(String id);
}
