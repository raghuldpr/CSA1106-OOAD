package edu.simats.ccrs.service;

import edu.simats.ccrs.domain.*;
import edu.simats.ccrs.domain.rules.*;
import edu.simats.ccrs.repository.memory.*;
import edu.simats.ccrs.service.notification.EmailNotifier;
import edu.simats.ccrs.service.notification.SmsNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Service tests run against in-memory repositories: no database needed (testability via DIP). */
class RegistrationServiceTest {
    InMemoryStudentRepository students; InMemoryCourseRepository courses; InMemoryRegistrationRepository regs;
    Faculty f = new Faculty("F1", "Dr X", "x@uni.edu", "IT");

    @BeforeEach void setUp() {
        students = new InMemoryStudentRepository(); courses = new InMemoryCourseRepository(); regs = new InMemoryRegistrationRepository();
        students.create(new Student("S1", "Asha", "a@uni.edu", "CSE", 3));
        for (int i = 1; i <= 6; i++) courses.create(new Course("CSA110" + i, "Course " + i, 3, 30, f));
    }
    private RegistrationService service(List<RegistrationRule> rules) {
        return new RegistrationService(students, courses, regs, rules);
    }
    @Test void registersAndNotifiesAllObservers() {
        RegistrationService rs = service(List.of(new SeatAvailableRule(), new NoDuplicateRule()));
        EmailNotifier email = new EmailNotifier(); SmsNotifier sms = new SmsNotifier();
        rs.addListener(email); rs.addListener(sms);
        Registration r = rs.register("S1", "CSA1101");
        assertEquals("R0001", r.getId());
        assertEquals(1, email.sent().size());
        assertEquals(1, sms.sent().size());
    }
    @Test void duplicateRegistrationIsRefused() {
        RegistrationService rs = service(List.of(new NoDuplicateRule()));
        rs.register("S1", "CSA1101");
        assertThrows(DomainException.class, () -> rs.register("S1", "CSA1101"));
    }
    @Test void businessRuleChange_maxFiveCourses_boundary() {
        RegistrationService rs = service(List.of(new NoDuplicateRule(), new MaxCoursesRule(5)));
        for (int i = 1; i <= 5; i++) rs.register("S1", "CSA110" + i);       // 5th is allowed (boundary)
        DomainException e = assertThrows(DomainException.class, () -> rs.register("S1", "CSA1106")); // 6th refused
        assertTrue(e.getMessage().contains("limit 5"));
    }
    @Test void withoutTheNewRuleSixCoursesAreAllowed() {
        RegistrationService rs = service(List.of(new NoDuplicateRule()));
        for (int i = 1; i <= 6; i++) rs.register("S1", "CSA110" + i);
        assertEquals(6, rs.coursesOf("S1").size());
    }
    @Test void missingObjectsGiveNotFound() {
        RegistrationService rs = service(List.of());
        assertThrows(NotFoundException.class, () -> rs.register("S404", "CSA1101"));
        assertThrows(NotFoundException.class, () -> rs.register("S1", "XYZ0000"));
        assertThrows(NotFoundException.class, () -> rs.drop("R9999"));
    }
}
