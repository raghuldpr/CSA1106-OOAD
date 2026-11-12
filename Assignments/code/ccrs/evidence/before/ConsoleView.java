package edu.simats.ccrs.view;

import edu.simats.ccrs.controller.RegistrationController;
import edu.simats.ccrs.controller.Result;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/** View: reads commands and displays Results. Knows only the controller. */
public class ConsoleView {
    private final RegistrationController controller;
    private final PrintStream out;
    public ConsoleView(RegistrationController controller, PrintStream out) { this.controller = controller; this.out = out; }

    public void run(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            out.println("> " + line);
            String[] a = line.split("\\|", -1);
            Result r = switch (a[0].trim().toLowerCase()) {
                case "enrol"    -> controller.enrolStudent(arg(a, 1), arg(a, 2), arg(a, 3), arg(a, 4), arg(a, 5));
                case "register" -> controller.register(arg(a, 1), arg(a, 2));
                case "drop"     -> controller.drop(arg(a, 1));
                case "list"     -> controller.listCourses(arg(a, 1));
                default         -> Result.error("Unknown command: " + a[0]);
            };
            out.println((r.ok() ? "  OK    " : "  ERROR ") + r.message());
        }
    }
    private static String arg(String[] a, int i) { return i < a.length ? a[i] : null; }
}
