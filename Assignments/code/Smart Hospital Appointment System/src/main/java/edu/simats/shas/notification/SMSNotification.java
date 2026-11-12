package edu.simats.shas.notification;

public class SMSNotification extends Notification {
    private final String phone;
    private final SmsGateway gateway;
    public SMSNotification(String message, String phone, SmsGateway gateway) {
        super(message); this.phone = phone; this.gateway = gateway;
    }
    @Override protected void deliver(String message) { gateway.sendSms(phone, message); }
    @Override public String channel() { return "SMS"; }
    public String getPhone() { return phone; }
}
