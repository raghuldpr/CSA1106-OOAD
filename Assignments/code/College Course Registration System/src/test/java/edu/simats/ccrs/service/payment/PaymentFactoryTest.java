package edu.simats.ccrs.service.payment;

import edu.simats.ccrs.domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentFactoryTest {
    @Test void createsTheRightConcreteTypeForEachMethod() {
        PaymentFactory f = new PaymentFactory();
        assertInstanceOf(CardPayment.class, f.create("card", "S1", 100, "4111111111111111"));
        assertInstanceOf(UpiPayment.class, f.create("UPI", "S1", 100, "asha@okbank"));
    }
    @Test void unsupportedMethodIsRejected() {
        DomainException e = assertThrows(DomainException.class, () -> new PaymentFactory().create("WALLET", "S1", 100, "x"));
        assertTrue(e.getMessage().contains("unsupported"));
    }
    @Test void changeRequirement_newWalletMethodWithoutChangingExistingClasses() {
        PaymentFactory f = new PaymentFactory();
        f.register("WALLET", WalletPayment::new);           // only new code
        Payment p = new PaymentService(f).payFee("S1", 4, "WALLET", "asha-wallet");
        assertEquals("WALLET", p.method());
        assertEquals(6000.0, p.getAmount());
        assertEquals(Payment.Status.PAID, p.getStatus());
    }
    @Test void declinedUpiPaymentIsMarkedFailed() {
        Payment p = new PaymentService(new PaymentFactory()).payFee("S1", 3, "UPI", "asha@fail");
        assertEquals(Payment.Status.FAILED, p.getStatus());
    }
    /** New payment type written for the change requirement. */
    static class WalletPayment extends Payment {
        WalletPayment(String id, String sid, double amt, String wallet) { super(id, sid, amt); }
        @Override protected boolean authorise() { return true; }
        @Override public String method() { return "WALLET"; }
    }
}
