package edu.simats.shas;

import java.time.LocalDateTime;

public class Payment {
    public enum Status { PENDING, PAID, FAILED, REFUNDED }
    private final String paymentId;
    private final double amount;
    private Status status = Status.PENDING;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;

    public Payment(String paymentId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        this.paymentId = paymentId; this.amount = amount;
    }
    public boolean pay(PaymentGateway gateway, LocalDateTime now) {
        if (status != Status.PENDING && status != Status.FAILED)
            throw new InvalidTransitionException("payment " + paymentId + " is " + status);
        boolean ok = gateway.charge(paymentId, amount);
        status = ok ? Status.PAID : Status.FAILED;
        if (ok) paidAt = now;
        return ok;
    }
    public void refund(PaymentGateway gateway, LocalDateTime now) {
        if (status != Status.PAID) throw new InvalidTransitionException("only a PAID payment can be refunded, was " + status);
        if (!gateway.refund(paymentId, amount)) throw new BusinessRuleException("refund rejected by gateway");
        status = Status.REFUNDED;
        refundedAt = now;
    }
    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public Status getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getRefundedAt() { return refundedAt; }
}
