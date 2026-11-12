package edu.simats.ccrs.config;

/** Singleton holding application-wide configuration. */
public final class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final double feePerCredit = 1500.0;
    private final int maxCoursesPerStudent = 5;
    private AppConfig() { }
    public static AppConfig getInstance() { return INSTANCE; }
    public double feePerCredit() { return feePerCredit; }
    public int maxCoursesPerStudent() { return maxCoursesPerStudent; }
}
