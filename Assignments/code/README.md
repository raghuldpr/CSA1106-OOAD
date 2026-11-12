# CSA1106 – runnable code for Assignments 4 and 5

Requires JDK 17+ and Maven. Both reports (Appendix A) reproduce this code.

## ccrs – Assignment 4 (College Course Registration System, ObjectDB)

```
cd ccrs
mvn test                                   # 29 tests
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes;$(cat cp.txt)" edu.simats.ccrs.App db/ccrs.odb demo/script.txt
```
(On Linux/macOS use `:` instead of `;` in the classpath.) ObjectDB is downloaded from https://m2.objectdb.com.

## shas – Assignment 5 (Smart Hospital Appointment System core)

```
cd shas
mvn test                                   # 28 tests + JaCoCo report in target/site/jacoco
python tools/mutants.py . v2               # runs the 3 seeded mutants (needs a prior mvn test)
```

`evidence/` in each project holds the outputs quoted in the reports.
