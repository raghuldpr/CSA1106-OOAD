package edu.simats.ccrs.service.payment;

import edu.simats.ccrs.domain.CardPayment;
import edu.simats.ccrs.domain.DomainException;
import edu.simats.ccrs.domain.Payment;
import edu.simats.ccrs.domain.UpiPayment;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/** Factory: separates creation of concrete Payment objects from the client code. */
public class PaymentFactory {
    @FunctionalInterface
    public interface Creator { Payment create(String id, String studentId, double amount, String detail); }

    private final Map<String, Creator> creators = new TreeMap<>();

    public PaymentFactory() {
        register("CARD", CardPayment::new);
        register("UPI", UpiPayment::new);
    }
    /** Extension point used by the change requirement (new payment method). */
    public void register(String method, Creator creator) { creators.put(method.toUpperCase(), creator); }

    public Payment create(String method, String studentId, double amount, String detail) {
        Creator c = creators.get(method == null ? "" : method.toUpperCase());
        if (c == null) throw new DomainException("unsupported payment method: " + method + " (supported: " + creators.keySet() + ")");
        return c.create("P-" + UUID.randomUUID().toString().substring(0, 8), studentId, amount, detail);
    }
}
