package edu.simats.ccrs.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseAndRegistrationTest {
    private final Faculty f = new Faculty("F1", "Dr X", "x@uni.edu", "IT");

    @Test void registrationOccupiesASeatAndDropReleasesIt() {
        Course c = new Course("CSA1106", "OOAD", 4, 2, f);
        Student s = new Student("S1", "Asha", "a@uni.edu", "CSE", 3);
        Registration r = Registration.create("R1", s, c);
        assertEquals(1, c.getEnrolled());
        r.drop();
        assertEquals(0, c.getEnrolled());
        assertEquals(RegistrationStatus.DROPPED, r.getStatus());
    }
    @Test void fullCourseRefusesRegistration_boundaryAtCapacity() {
        Course c = new Course("CSA1605", "Cloud", 3, 1, f);
        Registration.create("R1", new Student("S1", "A", "a@uni.edu", "CSE", 1), c);
        assertFalse(c.hasSeat());
        assertThrows(DomainException.class, () -> Registration.create("R2", new Student("S2", "B", "b@uni.edu", "CSE", 1), c));
    }
    @Test void droppingTwiceIsRejected() {
        Registration r = Registration.create("R1", new Student("S1", "A", "a@uni.edu", "CSE", 1), new Course("CSA1106", "OOAD", 4, 5, f));
        r.drop();
        assertThrows(DomainException.class, r::drop);
    }
    @Test void invalidDomainDataIsRejected() {
        assertThrows(DomainException.class, () -> new Student("S1", "A", "not-an-email", "CSE", 1));
        assertThrows(DomainException.class, () -> new Student("S1", "A", "a@uni.edu", "CSE", 0));
        assertThrows(DomainException.class, () -> new Course("bad", "X", 4, 5, f));
    }
    @Test void paymentCannotBePaidTwice() {
        Payment p = new CardPayment("P1", "S1", 6000, "4111111111111111");
        assertTrue(p.pay());
        assertThrows(DomainException.class, p::pay);
        assertEquals("XXXX-XXXX-XXXX-1111", ((CardPayment) p).getMaskedCard());
    }
}
