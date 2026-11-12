package edu.simats.shas;

import edu.simats.shas.TestSupport.*;
import edu.simats.shas.notification.NotificationFactory;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/** Tests added during the coverage gap analysis (B1.3). */
class CoverageGapTest {
    final MutableClock clock = new MutableClock(TestSupport.BOOKED_AT);
    final StubPaymentGateway gw = new StubPaymentGateway();
    final NotificationFactory nf = new NotificationFactory(new RecordingSms(), new RecordingEmail());
    final Patient patient = new Patient("P1", "9000000001", "p1@example.org", Channel.SMS);
    final Slot slot = new Slot("S1", LocalDateTime.of(2026, 11, 16, 12, 0), LocalDateTime.of(2026, 11, 16, 12, 15));

    @Test void declinedPaymentIsFailedAndCannotConfirmButCanBeRetried() {
        Appointment a = Appointment.book("A1", patient, slot, 500, clock);
        Payment p = new Payment("PAY1", 500);
        gw.approve = false;
        assertFalse(p.pay(gw, LocalDateTime.now(clock)));
        assertEquals(Payment.Status.FAILED, p.getStatus());
        assertThrows(BusinessRuleException.class, () -> a.confirm(p, nf));
        gw.approve = true;
        assertTrue(p.pay(gw, LocalDateTime.now(clock)));             // retry allowed from FAILED
        a.confirm(p, nf);
        assertEquals(AppointmentStatus.CONFIRMED, a.getStatus());
    }
    @Test void underpaymentIsRejected() {
        Appointment a = Appointment.book("A1", patient, slot, 500, clock);
        Payment p = new Payment("PAY1", 499.99); p.pay(gw, LocalDateTime.now(clock));
        assertThrows(BusinessRuleException.class, () -> a.confirm(p, nf));
    }
    @Test void onlyPaidPaymentsCanBeRefundedAndPaidOnesCannotBePaidAgain() {
        Payment p = new Payment("PAY1", 500);
        assertThrows(InvalidTransitionException.class, () -> p.refund(gw, LocalDateTime.now(clock)));
        p.pay(gw, LocalDateTime.now(clock));
        assertThrows(InvalidTransitionException.class, () -> p.pay(gw, LocalDateTime.now(clock)));
    }
    @Test void expiryDoesNothingInsideTheWindowOrAfterConfirmation() {
        Appointment a = Appointment.book("A1", patient, slot, 500, clock);
        clock.advanceMinutes(15);
        assertFalse(a.expireIfUnpaid());                                // exactly 15 minutes: still valid
        Payment p = new Payment("PAY1", 500); p.pay(gw, LocalDateTime.now(clock));
        a.confirm(p, nf);
        clock.advanceMinutes(60);
        assertFalse(a.expireIfUnpaid());
        assertEquals(AppointmentStatus.CONFIRMED, a.getStatus());
    }
    @Test void noShowBeforeThirtyMinutesAfterStartIsRefused() {
        Appointment a = Appointment.book("A1", patient, slot, 500, clock);
        Payment p = new Payment("PAY1", 500); p.pay(gw, LocalDateTime.now(clock));
        a.confirm(p, nf);
        clock.set(LocalDateTime.of(2026, 11, 16, 12, 29));
        assertThrows(BusinessRuleException.class, a::markNoShow);
    }
    @Test void invalidConstructionIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Payment("P", 0));
        LocalDateTime t = LocalDateTime.of(2026, 11, 16, 12, 0);
        assertThrows(IllegalArgumentException.class, () -> new Slot("S", t, t));
        assertThrows(IllegalArgumentException.class, () -> Appointment.book("A", patient, slot, 0, clock));
    }
}
