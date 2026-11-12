package edu.simats.ccrs.service.notification;

import edu.simats.ccrs.domain.Registration;
import edu.simats.ccrs.domain.events.RegistrationListener;
import java.util.ArrayList;
import java.util.List;

/** Concrete observer (e-mail gateway stub records the messages). */
public class EmailNotifier implements RegistrationListener {
    private final List<String> sent = new ArrayList<>();
    @Override public void onRegistered(Registration r) {
        sent.add("EMAIL to " + r.getStudent().getEmail() + ": registered for " + r.getCourse().getCode());
    }
    public List<String> sent() { return sent; }
}
