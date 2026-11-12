package edu.simats.shas.notification;

public class EmailNotification extends Notification {
    private final String emailId;
    private final EmailGateway gateway;
    public EmailNotification(String message, String emailId, EmailGateway gateway) {
        super(message); this.emailId = emailId; this.gateway = gateway;
    }
    @Override protected void deliver(String message) { gateway.sendEmail(emailId, "SHAS appointment", message); }
    @Override public String channel() { return "EMAIL"; }
    public String getEmailId() { return emailId; }
}
