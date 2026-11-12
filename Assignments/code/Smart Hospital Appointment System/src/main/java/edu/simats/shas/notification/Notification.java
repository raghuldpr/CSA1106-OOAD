package edu.simats.shas.notification;

import java.time.LocalDateTime;

/** Abstract notification; concrete channels override send() (polymorphism, Open-Closed). */
public abstract class Notification {
    private final String message;
    private LocalDateTime sentAt;

    protected Notification(String message) { this.message = message; }

    public final void send(LocalDateTime now) {
        if (sentAt != null) throw new IllegalStateException("notification already sent");
        deliver(message);
        sentAt = now;
    }
    protected abstract void deliver(String message);
    public abstract String channel();
    public String getMessage() { return message; }
    public LocalDateTime getSentAt() { return sentAt; }
}
