package edu.simats.ccrs.repository;

/** Single place for the registration identifier format (refactored out of both repositories). */
public final class RegistrationIds {
    private RegistrationIds() { }
    /** @param lastId highest existing id such as "R0007", or null when none exist */
    public static String after(String lastId) {
        int n = (lastId == null) ? 0 : Integer.parseInt(lastId.substring(1));
        return String.format("R%04d", n + 1);
    }
}
