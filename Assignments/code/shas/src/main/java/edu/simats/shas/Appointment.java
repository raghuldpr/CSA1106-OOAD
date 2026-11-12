package edu.simats.shas;

import edu.simats.shas.notification.Notification;
import edu.simats.shas.notification.NotificationFactory;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Appointment life cycle (A2.2) implementing BR1, BR2, BR3, BR4 and BR5.
 * All state is private; every transition is checked and invalid ones throw.
 */
public class Appointment {
    public static final Duration PAYMENT_WINDOW = Duration.ofMinutes(15);   // BR3
    public static final long CANCEL_LIMIT_MINUTES = 120;                    // BR5 (2 hours)
    public static final long NO_SHOW_AFTER_MINUTES = 30;                    // BR6

    private final String appointmentId;
    private final Patient patient;
    private final Slot slot;
    private final double fee;
    private final LocalDateTime createdAt;
    private final Clock clock;
    private AppointmentStatus status;
    private Payment payment;
    private Notification lastNotification;

    private Appointment(String id, Patient patient, Slot slot, double fee, Clock clock) {
        this.appointmentId = id; this.patient = patient; this.slot = slot; this.fee = fee;
        this.clock = clock; this.createdAt = now(); this.status = AppointmentStatus.REQUESTED;
    }

    /** Books a slot: BR2 (max 3 active), BR1 (slot held once), BR3 (starts Requested). */
    public static Appointment book(String id, Patient patient, Slot slot, double fee, Clock clock) {
        if (fee <= 0) throw new IllegalArgumentException("fee must be positive");
        if (patient.getActiveCount() >= Patient.MAX_ACTIVE)
            throw new BusinessRuleException("BR2: patient already has " + Patient.MAX_ACTIVE + " active appointments");
        slot.hold();
        patient.addActive();
        return new Appointment(id, patient, slot, fee, clock);
    }

    /** BR3/BR4: a paid fee within 15 minutes confirms and notifies on the preferred channel. */
    public void confirm(Payment p, NotificationFactory notifications) {
        requireStatus(AppointmentStatus.REQUESTED, "confirm");
        if (paymentWindowExpired()) throw new BusinessRuleException("BR3: payment window of 15 minutes has expired");
        if (p == null || p.getStatus() != Payment.Status.PAID) throw new BusinessRuleException("payment is not PAID");
        if (p.getAmount() < fee) throw new BusinessRuleException("payment " + p.getAmount() + " is less than fee " + fee);
        this.payment = p;
        status = AppointmentStatus.CONFIRMED;
        lastNotification = notifications.forPatient(patient, "Appointment " + appointmentId + " confirmed for " + slot.getStartTime());
        lastNotification.send(now());
    }

    /** BR3: called by a scheduler; cancels an unpaid Requested appointment after 15 minutes. */
    public boolean expireIfUnpaid() {
        if (status != AppointmentStatus.REQUESTED || !paymentWindowExpired()) return false;
        toCancelled();
        return true;
    }

    /** BR5: patient cancellation. Requested: always. Confirmed: only >= 2 h before start, with refund. */
    public void cancel(PaymentGateway gateway) {
        if (status == AppointmentStatus.REQUESTED) { toCancelled(); return; }
        requireStatus(AppointmentStatus.CONFIRMED, "cancel");
        long minutesBefore = Duration.between(now(), slot.getStartTime()).toMinutes();
        if (minutesBefore >= CANCEL_LIMIT_MINUTES) {
            payment.refund(gateway, now());
            toCancelled();
        } else {
            throw new BusinessRuleException("BR5: cancellation refused " + minutesBefore + " minutes before the slot");
        }
    }

    /** BR6: the doctor marks a Confirmed appointment Completed. */
    public void complete() {
        requireStatus(AppointmentStatus.CONFIRMED, "complete");
        status = AppointmentStatus.COMPLETED;
        patient.removeActive();
    }

    /** BR6: receptionist marks NoShow at least 30 minutes after the slot start. */
    public void markNoShow() {
        requireStatus(AppointmentStatus.CONFIRMED, "markNoShow");
        if (now().isBefore(slot.getStartTime().plusMinutes(NO_SHOW_AFTER_MINUTES)))
            throw new BusinessRuleException("BR6: no-show can be marked only 30 minutes after the slot start");
        status = AppointmentStatus.NO_SHOW;
        patient.removeActive();
    }

    private void toCancelled() {
        status = AppointmentStatus.CANCELLED;
        slot.release();
        patient.removeActive();
    }
    private boolean paymentWindowExpired() { return now().isAfter(createdAt.plus(PAYMENT_WINDOW)); }
    private void requireStatus(AppointmentStatus expected, String op) {
        if (status != expected)
            throw new InvalidTransitionException(op + "() is not allowed in state " + status);
    }
    private LocalDateTime now() { return LocalDateTime.now(clock); }

    public String getAppointmentId() { return appointmentId; }
    public AppointmentStatus getStatus() { return status; }
    public Slot getSlot() { return slot; }
    public Patient getPatient() { return patient; }
    public Payment getPayment() { return payment; }
    public double getFee() { return fee; }
    public Notification getLastNotification() { return lastNotification; }
}
