package edu.simats.ccrs.service;

import edu.simats.ccrs.domain.*;
import edu.simats.ccrs.domain.events.RegistrationListener;
import edu.simats.ccrs.domain.rules.RegistrationRule;
import edu.simats.ccrs.repository.CourseRepository;
import edu.simats.ccrs.repository.RegistrationRepository;
import edu.simats.ccrs.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;

/** Application service: coordinates the Register Course use case. No persistence-specific code. */
public class RegistrationService {
    private final StudentRepository students;
    private final CourseRepository courses;
    private final RegistrationRepository registrations;
    private final List<RegistrationRule> rules;
    private final List<RegistrationListener> listeners = new ArrayList<>();

    public RegistrationService(StudentRepository students, CourseRepository courses,
                               RegistrationRepository registrations, List<RegistrationRule> rules) {
        this.students = students; this.courses = courses;
        this.registrations = registrations; this.rules = List.copyOf(rules);
    }
    public void addListener(RegistrationListener l) { listeners.add(l); }

    public Registration register(String studentId, String courseCode) {
        Student s = students.findById(studentId)
                .orElseThrow(() -> new NotFoundException("student not found: " + studentId));
        Course c = courses.findByCode(courseCode)
                .orElseThrow(() -> new NotFoundException("course not found: " + courseCode));
        List<Registration> active = registrations.findActiveByStudent(studentId);
        for (RegistrationRule rule : rules) rule.check(s, c, active);          // Strategy
        Registration r = Registration.create(registrations.nextId(), s, c);   // domain logic
        registrations.create(r);                                              // persistence
        listeners.forEach(l -> l.onRegistered(r));                            // Observer
        return r;
    }
    public void drop(String registrationId) {
        Registration r = registrations.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("registration not found: " + registrationId));
        r.drop();
        registrations.update(r);
    }
    public List<Registration> coursesOf(String studentId) {
        if (students.findById(studentId).isEmpty()) throw new NotFoundException("student not found: " + studentId);
        return registrations.findActiveByStudent(studentId);
    }
    public List<Course> availableCourses() { return courses.findAll(); }
}
