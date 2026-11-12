package edu.simats.ccrs.domain.rules;

import edu.simats.ccrs.domain.*;
import java.util.List;

public class NoDuplicateRule implements RegistrationRule {
    @Override public void check(Student s, Course c, List<Registration> active) {
        boolean dup = active.stream().anyMatch(r -> r.getCourse().getCode().equals(c.getCode()));
        if (dup) throw new DomainException(s.getId() + " is already registered for " + c.getCode());
    }
}
