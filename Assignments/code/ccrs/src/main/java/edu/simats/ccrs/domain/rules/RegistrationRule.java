package edu.simats.ccrs.domain.rules;

import edu.simats.ccrs.domain.Course;
import edu.simats.ccrs.domain.Registration;
import edu.simats.ccrs.domain.Student;
import java.util.List;

/** Strategy: one replaceable business rule checked before a registration is created. */
public interface RegistrationRule {
    void check(Student student, Course course, List<Registration> activeRegistrations);
}
