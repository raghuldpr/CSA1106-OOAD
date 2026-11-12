package edu.simats.shas;

import edu.simats.shas.TestSupport.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/** BR2 (2, 3 and 4 active appointments) and BR1 (one appointment per slot). */
class ActiveLimitAndSlotTest {
    final MutableClock clock = new MutableClock(TestSupport.BOOKED_AT);
    final Patient patient = new Patient("P1", "9000000001", "p1@example.org", Channel.SMS);
    int n = 0;
    Slot newSlot() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 17, 9, 0).plusMinutes(15L * n++);
        return new Slot("S" + n, t, t.plusMinutes(15));
    }
    @Test void secondAndThirdActiveAppointmentsAreAllowed() {
        Appointment.book("A1", patient, newSlot(), 500, clock);
        Appointment.book("A2", patient, newSlot(), 500, clock);
        assertEquals(2, patient.getActiveCount());
        Appointment.book("A3", patient, newSlot(), 500, clock);
        assertEquals(3, patient.getActiveCount());
    }
    @Test void fourthActiveAppointmentIsRefused() {
        for (int i = 1; i <= 3; i++) Appointment.book("A" + i, patient, newSlot(), 500, clock);
        Slot fourth = newSlot();
        assertThrows(BusinessRuleException.class, () -> Appointment.book("A4", patient, fourth, 500, clock));
        assertTrue(fourth.isAvailable(), "a refused booking must not hold the slot");
    }
    @Test void aSlotCannotBeHeldByTwoAppointments() {
        Slot s = newSlot();
        Appointment.book("A1", patient, s, 500, clock);
        Patient other = new Patient("P2", "9000000002", "p2@example.org", Channel.EMAIL);
        assertThrows(BusinessRuleException.class, () -> Appointment.book("A2", other, s, 500, clock));
        assertEquals(0, other.getActiveCount());
    }
}
