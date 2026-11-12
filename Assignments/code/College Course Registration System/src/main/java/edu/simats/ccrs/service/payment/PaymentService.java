package edu.simats.ccrs.service.payment;

import edu.simats.ccrs.config.AppConfig;
import edu.simats.ccrs.domain.Payment;

public class PaymentService {
    private final PaymentFactory factory;
    public PaymentService(PaymentFactory factory) { this.factory = factory; }

    /** Fee = credits x fee per credit (from the single configuration object). */
    public Payment payFee(String studentId, int credits, String method, String detail) {
        double amount = credits * AppConfig.getInstance().feePerCredit();
        Payment p = factory.create(method, studentId, amount, detail);  // client never calls "new CardPayment"
        p.pay();                                                        // polymorphic behaviour
        return p;
    }
}
