package edu.simats.ccrs;

import edu.simats.ccrs.config.AppConfig;
import edu.simats.ccrs.controller.RegistrationController;
import edu.simats.ccrs.domain.Course;
import edu.simats.ccrs.domain.Faculty;
import edu.simats.ccrs.domain.rules.MaxCoursesRule;
import edu.simats.ccrs.domain.rules.NoDuplicateRule;
import edu.simats.ccrs.domain.rules.SeatAvailableRule;
import edu.simats.ccrs.repository.CourseRepository;
import edu.simats.ccrs.repository.objectdb.*;
import edu.simats.ccrs.service.RegistrationService;
import edu.simats.ccrs.service.StudentService;
import edu.simats.ccrs.service.notification.EmailNotifier;
import edu.simats.ccrs.service.notification.SmsNotifier;
import edu.simats.ccrs.view.ConsoleView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Composition root: the only class that knows every concrete implementation. */
public class App {
    public static void main(String[] args) throws Exception {
        String dbPath = args.length > 0 ? args[0] : "db/ccrs.odb";
        try (ObjectDbContext db = new ObjectDbContext(dbPath)) {
            CourseRepository courses = new ObjectDbCourseRepository(db);
            if (courses.findAll().isEmpty()) seed(courses);
            RegistrationService rs = new RegistrationService(new ObjectDbStudentRepository(db), courses,
                    new ObjectDbRegistrationRepository(db),
                    List.of(new SeatAvailableRule(), new NoDuplicateRule(),
                            new MaxCoursesRule(AppConfig.getInstance().maxCoursesPerStudent())));
            EmailNotifier email = new EmailNotifier();
            SmsNotifier sms = new SmsNotifier();
            rs.addListener(email);
            rs.addListener(sms);
            RegistrationController controller = new RegistrationController(new StudentService(new ObjectDbStudentRepository(db)), rs);
            Reader src = args.length > 1 ? new FileReader(args[1], StandardCharsets.UTF_8) : new InputStreamReader(System.in);
            new ConsoleView(controller, System.out).run(new BufferedReader(src));
            System.out.println("Notifications sent: " + email.sent().size() + " e-mail, " + sms.sent().size() + " SMS");
        }
    }
    static void seed(CourseRepository courses) {
        Faculty f = new Faculty("F01", "Dr. Faculty Placeholder", "faculty@example.edu", "IT");
        courses.create(new Course("CSA1106", "Object-Oriented Analysis and Design", 4, 60, f));
        courses.create(new Course("CSA1201", "Database Systems", 4, 60, f));
        courses.create(new Course("CSA1302", "Computer Networks", 3, 60, f));
        courses.create(new Course("CSA1403", "Operating Systems", 4, 60, f));
        courses.create(new Course("CSA1504", "Software Testing", 3, 60, f));
        courses.create(new Course("CSA1605", "Cloud Computing", 3, 2, f));
    }
}
