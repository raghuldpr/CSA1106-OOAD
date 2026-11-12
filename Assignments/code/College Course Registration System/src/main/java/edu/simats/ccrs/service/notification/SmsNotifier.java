package edu.simats.ccrs.service.notification;

import edu.simats.ccrs.domain.Registration;
import edu.simats.ccrs.domain.events.RegistrationListener;
import java.util.ArrayList;
import java.util.List;

/** Added for the change requirement "SMS in addition to e-mail": no other class changed. */
public class SmsNotifier implements RegistrationListener {
    private final List<String> sent = new ArrayList<>();
    @Override public void onRegistered(Registration r) {
        sent.add("SMS to student " + r.getStudent().getId() + ": " + r.getCourse().getCode() + " confirmed");
    }
    public List<String> sent() { return sent; }
}
