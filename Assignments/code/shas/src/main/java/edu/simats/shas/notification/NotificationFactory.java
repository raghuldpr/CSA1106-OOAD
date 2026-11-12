package edu.simats.shas.notification;

import edu.simats.shas.Channel;
import edu.simats.shas.Patient;

/** Chooses the concrete notification from the patient's preferred channel (BR4). */
public class NotificationFactory {
    private final SmsGateway sms;
    private final EmailGateway email;
    public NotificationFactory(SmsGateway sms, EmailGateway email) { this.sms = sms; this.email = email; }

    public Notification forPatient(Patient p, String message) {
        if (p.getPreferredChannel() == Channel.SMS) return new SMSNotification(message, p.getPhone(), sms);
        return new EmailNotification(message, p.getEmail(), email);
    }
}
