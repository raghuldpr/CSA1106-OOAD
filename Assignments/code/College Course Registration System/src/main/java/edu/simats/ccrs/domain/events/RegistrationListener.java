package edu.simats.ccrs.domain.events;

import edu.simats.ccrs.domain.Registration;

/** Observer: notified after a registration is committed. */
public interface RegistrationListener {
    void onRegistered(Registration registration);
}
