package edu.simats.shas;

import edu.simats.shas.TestSupport.*;
import edu.simats.shas.notification.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NotificationPolymorphismTest {
    @Test void sendThroughBaseClassReferenceUsesTheConcreteChannel() {
        RecordingSms sms = new RecordingSms(); RecordingEmail email = new RecordingEmail();
        List<Notification> all = List.of(
                new SMSNotification("hello", "9000000001", sms),
                new EmailNotification("hello", "p@example.org", email));
        for (Notification n : all) n.send(LocalDateTime.of(2026, 11, 16, 8, 0));   // dynamic binding
        assertEquals(List.of("9000000001|hello"), sms.sent);
        assertEquals(List.of("p@example.org|hello"), email.sent);
        assertEquals("SMS", all.get(0).channel());
        assertEquals("EMAIL", all.get(1).channel());
    }
    @Test void aNotificationCannotBeSentTwice() {
        Notification n = new SMSNotification("x", "9", new RecordingSms());
        n.send(LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> n.send(LocalDateTime.now()));
    }
}
