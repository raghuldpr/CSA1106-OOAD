import shutil, subprocess, sys, os, re, json
# usage: mutants.py <project_dir> <label>   -> runs the test suite against 3 seeded mutants
proj, label = sys.argv[1], sys.argv[2]
MUT = {
 'M1': ('src/main/java/edu/simats/shas/Appointment.java',
        'if (minutesBefore >= CANCEL_LIMIT_MINUTES) {',
        'if (minutesBefore > CANCEL_LIMIT_MINUTES) {',
        'BR5 boundary: ">=" changed to ">" (cancellation exactly 120 minutes before is refused)'),
 'M2': ('src/main/java/edu/simats/shas/Appointment.java',
        '        if (status != AppointmentStatus.REQUESTED || !paymentWindowExpired()) return false;\n        toCancelled();',
        '        if (status != AppointmentStatus.REQUESTED || !paymentWindowExpired()) return false;\n        status = AppointmentStatus.CANCELLED; patient.removeActive();   // slot.release() missing',
        'BR3: slot not released when the payment window times out'),
 'M3': ('src/main/java/edu/simats/shas/notification/NotificationFactory.java',
        'if (p.getPreferredChannel() == Channel.SMS)',
        'if (p.getPreferredChannel() == Channel.EMAIL)',
        'BR4: e-mail sent when SMS is preferred (and SMS when e-mail is preferred)'),
}
results = {}
for mid, (f, old, new, desc) in MUT.items():
    work = os.path.join(proj, 'target', 'mut_' + mid)
    if os.path.exists(work): shutil.rmtree(work)
    shutil.copytree(os.path.join(proj, 'src'), os.path.join(work, 'src'))
    shutil.copy(os.path.join(proj, 'pom.xml'), work)
    p = os.path.join(work, f)
    s = open(p, encoding='utf8').read()
    assert s.count(old) == 1, (mid, 'pattern not unique')
    open(p, 'w', encoding='utf8').write(s.replace(old, new))
    r = subprocess.run('mvn -B -o test -Djacoco.skip=true', cwd=work, shell=True, capture_output=True, text=True)
    out = r.stdout
    tot = re.findall(r'Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)\s*$', out, re.M)
    failing = sorted(set(re.findall(r'\[ERROR\]\s+(\w+\.\w+(?:\[\d+\])?)(?::\d+)?\s', out)))
    t = tot[-1] if tot else ('?', '?', '?', '?')
    killed = r.returncode != 0
    results[mid] = dict(desc=desc, file=f, old=old.strip(), new=new.strip(), run=t[0], failures=t[1], errors=t[2],
                        killed=killed, failing=failing)
    print(mid, 'KILLED' if killed else 'SURVIVED', 'run', t[0], 'fail', t[1], 'err', t[2], failing)
json.dump(results, open(os.path.join(proj, 'evidence', f'mutants_{label}.json'), 'w'), indent=1)
k = sum(1 for v in results.values() if v['killed'])
print(f'mutation score {label}: {k}/{len(results)} = {100*k/len(results):.0f}%')
