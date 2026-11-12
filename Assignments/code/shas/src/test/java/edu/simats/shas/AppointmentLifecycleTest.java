package edu.simats.shas;

import edu.simats.shas.TestSupport.*;
import edu.simats.shas.notification.NotificationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentLifecycleTest {
    MutableClock clock;
    StubPaymentGateway gateway;
    RecordingSms sms;
    RecordingEmail email;
    NotificationFactory notifications;
    Patient patient;
    Slot slot;                       // slot starts at 12:00, booking at 08:00

    @BeforeEach void setUp() {
        clock = new MutableClock(TestSupport.BOOKED_AT);
        gateway = new StubPaymentGateway();
        sms = new RecordingSms(); email = new RecordingEmail();
        notifications = new NotificationFactory(sms, email);
        patient = new Patient("P1", "9000000001", "p1@example.org", Channel.SMS);
        slot = new Slot("S-12", LocalDateTime.of(2026, 11, 16, 12, 0), LocalDateTime.of(2026, 11, 16, 12, 15));
    }
    private Appointment book() { return Appointment.book("A1", patient, slot, 500, clock); }
    private Payment paid() { Payment p = new Payment("PAY1", 500); p.pay(gateway, LocalDateTime.now(clock)); return p; }
    private Appointment confirmed() { Appointment a = book(); a.confirm(paid(), notifications); return a; }

    // ---- valid transitions
    @Test void newAppointmentStartsRequestedAndHoldsTheSlot() {
        Appointment a = book();
        assertEquals(AppointmentStatus.REQUESTED, a.getStatus());
        assertFalse(slot.isAvailable());
        assertEquals(1, patient.getActiveCount());
    }
    @Test void requestedBecomesConfirmedWhenPaidWithin15Minutes() {
        Appointment a = book();
        clock.advanceMinutes(15);                       // exactly at the limit
        a.confirm(paid(), notifications);
        assertEquals(AppointmentStatus.CONFIRMED, a.getStatus());
    }
    @Test void requestedBecomesCancelledWhenPatientCancels() {
        Appointment a = book();
        a.cancel(gateway);
        assertEquals(AppointmentStatus.CANCELLED, a.getStatus());
        assertTrue(slot.isAvailable());
    }
    @Test void requestedIsCancelledAndSlotReleasedWhenPaymentTimesOut() {
        Appointment a = book();
        clock.advanceMinutes(16);
        assertTrue(a.expireIfUnpaid());
        assertEquals(AppointmentStatus.CANCELLED, a.getStatus());
        assertTrue(slot.isAvailable(), "slot must be released on payment time-out");
        assertEquals(0, patient.getActiveCount());
    }
    @Test void confirmedBecomesCompletedWhenDoctorMarksDone() {
        Appointment a = confirmed();
        a.complete();
        assertEquals(AppointmentStatus.COMPLETED, a.getStatus());
    }
    @Test void confirmedBecomesNoShowThirtyMinutesAfterStart() {
        Appointment a = confirmed();
        clock.set(LocalDateTime.of(2026, 11, 16, 12, 30));
        a.markNoShow();
        assertEquals(AppointmentStatus.NO_SHOW, a.getStatus());
    }
    @Test void confirmedCancelledEarlyEnoughGeneratesRefundAndReleasesSlot() {
        Appointment a = confirmed();
        a.cancel(gateway);
        assertEquals(AppointmentStatus.CANCELLED, a.getStatus());
        assertEquals(Payment.Status.REFUNDED, a.getPayment().getStatus());
        assertEquals(1, gateway.refunds.size());
        assertTrue(slot.isAvailable());
    }

    // ---- invalid (sneak-path) transitions
    @Test void cancellingACompletedAppointmentIsRejected() {
        Appointment a = confirmed(); a.complete();
        assertThrows(InvalidTransitionException.class, () -> a.cancel(gateway));
    }
    @Test void completingARequestedAppointmentIsRejected() {
        Appointment a = book();
        assertThrows(InvalidTransitionException.class, a::complete);
    }
    @Test void confirmingACancelledAppointmentIsRejected() {
        Appointment a = book(); a.cancel(gateway);
        assertThrows(InvalidTransitionException.class, () -> a.confirm(paid(), notifications));
    }
    @Test void markingNoShowOnARequestedAppointmentIsRejected() {
        Appointment a = book();
        clock.set(LocalDateTime.of(2026, 11, 16, 13, 0));
        assertThrows(InvalidTransitionException.class, a::markNoShow);
    }
    @Test void paymentAfterTheFifteenMinuteWindowIsRejected() {
        Appointment a = book();
        clock.advanceMinutes(16);
        assertThrows(BusinessRuleException.class, () -> a.confirm(paid(), notifications));
    }
}
