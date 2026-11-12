package edu.simats.shas;

import edu.simats.shas.TestSupport.*;
import edu.simats.shas.notification.NotificationFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/** BR5 boundary: 119, 120 and 121 minutes before the slot start. */
class CancellationBoundaryTest {
    @ParameterizedTest(name = "cancel {0} minutes before start -> allowed={1}")
    @CsvSource({ "119,false", "120,true", "121,true" })
    void confirmedCancellationRespectsTheTwoHourRule(long minutesBefore, boolean allowed) {
        LocalDateTime start = LocalDateTime.of(2026, 11, 16, 12, 0);
        MutableClock clock = new MutableClock(start.minusHours(4));
        StubPaymentGateway gw = new StubPaymentGateway();
        Patient p = new Patient("P1", "9000000001", "p1@example.org", Channel.EMAIL);
        Slot s = new Slot("S-12", start, start.plusMinutes(15));
        Appointment a = Appointment.book("A1", p, s, 500, clock);
        Payment pay = new Payment("PAY1", 500); pay.pay(gw, LocalDateTime.now(clock));
        a.confirm(pay, new NotificationFactory(new RecordingSms(), new RecordingEmail()));
        clock.set(start.minusMinutes(minutesBefore));
        if (allowed) {
            a.cancel(gw);
            assertEquals(AppointmentStatus.CANCELLED, a.getStatus());
            assertEquals(Payment.Status.REFUNDED, pay.getStatus());
        } else {
            assertThrows(BusinessRuleException.class, () -> a.cancel(gw));
            assertEquals(AppointmentStatus.CONFIRMED, a.getStatus());
        }
    }
}
