package edu.simats.ccrs.service;

import edu.simats.ccrs.domain.Student;
import edu.simats.ccrs.repository.StudentRepository;
import java.util.List;

public class StudentService {
    private final StudentRepository students;
    public StudentService(StudentRepository students) { this.students = students; }

    public Student enrol(String id, String name, String email, String programme, int semester) {
        Student s = new Student(id, name, email, programme, semester);   // domain validates
        students.create(s);
        return s;
    }
    public Student find(String id) {
        return students.findById(id).orElseThrow(() -> new NotFoundException("student not found: " + id));
    }
    public void changeEmail(String id, String email) {
        Student s = find(id);
        s.changeEmail(email);
        students.update(s);
    }
    public void remove(String id) { students.delete(id); }
    public List<Student> all() { return students.findAll(); }
}
