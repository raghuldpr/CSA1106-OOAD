package edu.simats.ccrs.domain.rules;

import edu.simats.ccrs.domain.*;
import java.util.List;

public class SeatAvailableRule implements RegistrationRule {
    @Override public void check(Student s, Course c, List<Registration> active) {
        if (!c.hasSeat()) throw new DomainException("course " + c.getCode() + " is full");
    }
}
