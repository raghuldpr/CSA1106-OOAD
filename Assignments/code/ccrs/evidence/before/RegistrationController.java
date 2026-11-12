package edu.simats.ccrs.controller;

import edu.simats.ccrs.domain.DomainException;
import edu.simats.ccrs.domain.Registration;
import edu.simats.ccrs.domain.Student;
import edu.simats.ccrs.repository.RepositoryException;
import edu.simats.ccrs.service.NotFoundException;
import edu.simats.ccrs.service.RegistrationService;
import edu.simats.ccrs.service.StudentService;
import java.util.stream.Collectors;

/** Controller: validates input, invokes services, converts outcomes into Results for the View. */
public class RegistrationController {
    private final StudentService studentService;
    private final RegistrationService registrationService;

    public RegistrationController(StudentService ss, RegistrationService rs) {
        this.studentService = ss; this.registrationService = rs;
    }
    public Result enrolStudent(String id, String name, String email, String programme, String semesterText) {
        if (isBlank(id) || isBlank(name) || isBlank(email) || isBlank(semesterText)) return Result.error("All fields are required");
        int sem;
        try { sem = Integer.parseInt(semesterText.trim()); }
        catch (NumberFormatException e) { return Result.error("Semester must be a number"); }
        if (sem < 1 || sem > 8) return Result.error("Semester must be between 1 and 8");
        return call(() -> {
            Student s = studentService.enrol(id.trim(), name.trim(), email.trim(), programme, sem);
            return "Student " + s.getId() + " enrolled";
        });
    }
    public Result register(String studentId, String courseCode) {
        if (isBlank(studentId) || isBlank(courseCode)) return Result.error("Student id and course code are required");
        return call(() -> {
            Registration r = registrationService.register(studentId.trim(), courseCode.trim().toUpperCase());
            return "Registration " + r.getId() + ": " + r.getStudent().getId() + " -> " + r.getCourse().getCode()
                    + " (seats " + r.getCourse().getEnrolled() + "/" + r.getCourse().getCapacity() + ")";
        });
    }
    public Result drop(String registrationId) {
        if (isBlank(registrationId)) return Result.error("Registration id is required");
        return call(() -> { registrationService.drop(registrationId.trim()); return "Dropped " + registrationId; });
    }
    public Result listCourses(String studentId) {
        if (isBlank(studentId)) return Result.error("Student id is required");
        return call(() -> registrationService.coursesOf(studentId.trim()).stream()
                .map(r -> r.getId() + " " + r.getCourse().getCode() + " " + r.getCourse().getTitle())
                .collect(Collectors.joining("; ", "Courses of " + studentId + ": [", "]")));
    }
    private Result call(java.util.function.Supplier<String> action) {
        try { return Result.ok(action.get()); }
        catch (NotFoundException | DomainException e) { return Result.error(e.getMessage()); }
        catch (RepositoryException e) { return Result.error("Storage unavailable, please retry later (" + e.getMessage() + ")"); }
    }
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
