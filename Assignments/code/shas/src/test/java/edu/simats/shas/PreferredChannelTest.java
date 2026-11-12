package edu.simats.shas;

import edu.simats.shas.TestSupport.*;
import edu.simats.shas.notification.NotificationFactory;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/** Added after mutant M3 survived: BR4 must use the patient's preferred channel. */
class PreferredChannelTest {
    private void confirmFor(Patient p, RecordingSms sms, RecordingEmail email) {
        MutableClock clock = new MutableClock(TestSupport.BOOKED_AT);
        StubPaymentGateway gw = new StubPaymentGateway();
        Slot s = new Slot("S1", LocalDateTime.of(2026, 11, 16, 12, 0), LocalDateTime.of(2026, 11, 16, 12, 15));
        Appointment a = Appointment.book("A1", p, s, 500, clock);
        Payment pay = new Payment("PAY1", 500); pay.pay(gw, LocalDateTime.now(clock));
        a.confirm(pay, new NotificationFactory(sms, email));
    }
    @Test void confirmationGoesBySmsWhenSmsIsPreferred() {
        RecordingSms sms = new RecordingSms(); RecordingEmail email = new RecordingEmail();
        confirmFor(new Patient("P1", "9000000001", "p1@example.org", Channel.SMS), sms, email);
        assertEquals(1, sms.sent.size());
        assertTrue(email.sent.isEmpty());
    }
    @Test void confirmationGoesByEmailWhenEmailIsPreferred() {
        RecordingSms sms = new RecordingSms(); RecordingEmail email = new RecordingEmail();
        confirmFor(new Patient("P2", "9000000002", "p2@example.org", Channel.EMAIL), sms, email);
        assertEquals(1, email.sent.size());
        assertTrue(sms.sent.isEmpty());
    }
}
