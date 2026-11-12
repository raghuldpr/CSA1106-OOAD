package edu.simats.ccrs.domain;

public class UpiPayment extends Payment {
    private String upiId;
    protected UpiPayment() { }
    public UpiPayment(String id, String studentId, double amount, String upiId) {
        super(id, studentId, amount);
        if (upiId == null || !upiId.contains("@")) throw new DomainException("invalid UPI id");
        this.upiId = upiId;
    }
    @Override protected boolean authorise() { return !upiId.endsWith("@fail"); } // stub: '@fail' simulates a decline
    @Override public String method() { return "UPI"; }
    public String getUpiId() { return upiId; }
}
