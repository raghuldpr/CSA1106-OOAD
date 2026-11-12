package edu.simats.ccrs.domain;

import java.time.LocalDateTime;

/** Association class between Student and Course. */
public class Registration {
    private String id;
    private Student student;
    private Course course;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;

    protected Registration() { }

    private Registration(String id, Student student, Course course) {
        this.id = id; this.student = student; this.course = course;
        this.status = RegistrationStatus.ACTIVE;
        this.registeredAt = LocalDateTime.now();
    }
    /** Factory method: the only way to create a registration; it occupies a seat. */
    public static Registration create(String id, Student student, Course course) {
        if (student == null || course == null) throw new DomainException("student and course are required");
        course.occupySeat();
        return new Registration(id, student, course);
    }
    public void drop() {
        if (status != RegistrationStatus.ACTIVE) throw new DomainException("registration " + id + " is not active");
        status = RegistrationStatus.DROPPED;
        course.releaseSeat();
    }
    public boolean isActive() { return status == RegistrationStatus.ACTIVE; }
    public String getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public RegistrationStatus getStatus() { return status; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
