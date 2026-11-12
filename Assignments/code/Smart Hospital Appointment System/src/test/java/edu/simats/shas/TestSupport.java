package edu.simats.shas;

import edu.simats.shas.notification.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/** Mutable test clock and recording stubs for the external gateways. */
class TestSupport {
    static final LocalDateTime BOOKED_AT = LocalDateTime.of(2026, 11, 16, 8, 0);
    static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    static class MutableClock extends Clock {
        private LocalDateTime now;
        MutableClock(LocalDateTime start) { now = start; }
        void set(LocalDateTime t) { now = t; }
        void advanceMinutes(long m) { now = now.plusMinutes(m); }
        @Override public ZoneId getZone() { return ZONE; }
        @Override public Clock withZone(ZoneId z) { return this; }
        @Override public Instant instant() { return now.atZone(ZONE).toInstant(); }
    }
    static class StubPaymentGateway implements PaymentGateway {
        boolean approve = true;
        final List<String> refunds = new ArrayList<>();
        @Override public boolean charge(String id, double amount) { return approve; }
        @Override public boolean refund(String id, double amount) { refunds.add(id + ":" + amount); return true; }
    }
    static class RecordingSms implements SmsGateway {
        final List<String> sent = new ArrayList<>();
        @Override public void sendSms(String phone, String text) { sent.add(phone + "|" + text); }
    }
    static class RecordingEmail implements EmailGateway {
        final List<String> sent = new ArrayList<>();
        @Override public void sendEmail(String to, String subject, String body) { sent.add(to + "|" + body); }
    }
}
