package edu.simats.ccrs.domain.rules;

import edu.simats.ccrs.domain.*;
import java.util.List;

/** Added for the business-rule change: a student cannot register for more than N courses. */
public class MaxCoursesRule implements RegistrationRule {
    private final int limit;
    public MaxCoursesRule(int limit) { this.limit = limit; }
    @Override public void check(Student s, Course c, List<Registration> active) {
        if (active.size() >= limit)
            throw new DomainException(s.getId() + " already has " + active.size() + " courses (limit " + limit + ")");
    }
}
