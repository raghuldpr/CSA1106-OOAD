package edu.simats.ccrs.controller;

import edu.simats.ccrs.domain.*;
import edu.simats.ccrs.domain.rules.*;
import edu.simats.ccrs.repository.memory.*;
import edu.simats.ccrs.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationControllerTest {
    RegistrationController c;
    @BeforeEach void setUp() {
        var students = new InMemoryStudentRepository(); var courses = new InMemoryCourseRepository();
        courses.create(new Course("CSA1106", "OOAD", 4, 1, new Faculty("F1", "Dr X", "x@uni.edu", "IT")));
        var rs = new RegistrationService(students, courses, new InMemoryRegistrationRepository(),
                List.of(new SeatAvailableRule(), new NoDuplicateRule()));
        c = new RegistrationController(new StudentService(students), rs);
    }
    @Test void missingInputIsRejectedBeforeReachingTheService() {
        assertFalse(c.enrolStudent("S1", "", "a@uni.edu", "CSE", "3").ok());
        assertFalse(c.register(null, "CSA1106").ok());
    }
    @Test void invalidAndBoundaryInput() {
        assertEquals("Semester must be a number", c.enrolStudent("S1", "A", "a@uni.edu", "CSE", "three").message());
        assertFalse(c.enrolStudent("S1", "A", "a@uni.edu", "CSE", "0").ok());
        assertFalse(c.enrolStudent("S1", "A", "a@uni.edu", "CSE", "9").ok());
        assertTrue(c.enrolStudent("S1", "A", "a@uni.edu", "CSE", "8").ok());      // upper boundary accepted
        assertTrue(c.enrolStudent("S2", "B", "b@uni.edu", "CSE", "1").ok());      // lower boundary accepted
        assertFalse(c.enrolStudent("S3", "C", "no-at-sign", "CSE", "2").ok());   // domain validation surfaced
    }
    @Test void normalOperationAndCapacityBoundary() {
        c.enrolStudent("S1", "A", "a@uni.edu", "CSE", "3");
        c.enrolStudent("S2", "B", "b@uni.edu", "CSE", "3");
        assertTrue(c.register("S1", "csa1106").ok());                        // lower-case normalised
        Result full = c.register("S2", "CSA1106");
        assertFalse(full.ok());
        assertTrue(full.message().contains("full"));
    }
}
