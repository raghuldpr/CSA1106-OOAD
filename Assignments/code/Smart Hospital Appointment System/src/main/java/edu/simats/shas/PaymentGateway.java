package edu.simats.shas;

/** External payment gateway (stubbed in tests). */
public interface PaymentGateway {
    boolean charge(String paymentId, double amount);
    boolean refund(String paymentId, double amount);
}
