package edu.simats.ccrs.domain;

public class CardPayment extends Payment {
    private String maskedCard;
    protected CardPayment() { }
    public CardPayment(String id, String studentId, double amount, String cardNumber) {
        super(id, studentId, amount);
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) throw new DomainException("card number must have 16 digits");
        this.maskedCard = "XXXX-XXXX-XXXX-" + cardNumber.substring(12);
    }
    @Override protected boolean authorise() { return true; }   // gateway stub
    @Override public String method() { return "CARD"; }
    public String getMaskedCard() { return maskedCard; }
}
