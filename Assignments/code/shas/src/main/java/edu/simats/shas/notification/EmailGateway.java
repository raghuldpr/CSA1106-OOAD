package edu.simats.shas.notification;

public interface EmailGateway { void sendEmail(String to, String subject, String body); }
