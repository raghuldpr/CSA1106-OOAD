package edu.simats.ccrs.domain;

/** Abstract fee payment; concrete types are created by PaymentFactory. */
public abstract class Payment {
    public enum Status { CREATED, PAID, FAILED }
    private String id;
    private String studentId;
    private double amount;
    private Status status = Status.CREATED;

    protected Payment() { }

    protected Payment(String id, String studentId, double amount) {
        if (amount <= 0) throw new DomainException("amount must be positive");
        this.id = id; this.studentId = studentId; this.amount = amount;
    }
    /** Template method: validation is common, authorisation is type-specific. */
    public final boolean pay() {
        if (status == Status.PAID) throw new DomainException("payment " + id + " already paid");
        boolean ok = authorise();
        status = ok ? Status.PAID : Status.FAILED;
        return ok;
    }
    protected abstract boolean authorise();
    public abstract String method();
    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public double getAmount() { return amount; }
    public Status getStatus() { return status; }
}
