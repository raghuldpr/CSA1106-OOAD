package edu.simats.ccrs.controller;

/** Response object returned to the View (no domain objects leak to the UI). */
public record Result(boolean ok, String message) {
    public static Result ok(String m) { return new Result(true, m); }
    public static Result error(String m) { return new Result(false, m); }
}
