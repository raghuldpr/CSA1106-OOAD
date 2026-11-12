package edu.simats.ccrs.repository.memory;

import edu.simats.ccrs.domain.Registration;
import edu.simats.ccrs.repository.RegistrationRepository;
import edu.simats.ccrs.repository.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryRegistrationRepository implements RegistrationRepository {
    private final Map<String, Registration> store = new TreeMap<>();
    @Override public void create(Registration r) {
        if (store.putIfAbsent(r.getId(), r) != null) throw new RepositoryException("duplicate registration " + r.getId());
    }
    @Override public Optional<Registration> findById(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<Registration> findActiveByStudent(String sid) {
        return store.values().stream().filter(r -> r.isActive() && r.getStudent().getId().equals(sid)).collect(Collectors.toList());
    }
    @Override public void update(Registration r) { store.put(r.getId(), r); }
    @Override public void delete(String id) {
        if (store.remove(id) == null) throw new RepositoryException("registration not found: " + id);
    }
    @Override public String nextId() { return String.format("R%04d", store.size() + 1); }
}
