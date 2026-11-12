package edu.simats.ccrs.repository.objectdb;

import edu.simats.ccrs.repository.RepositoryException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/** Owns the ObjectDB EntityManagerFactory and runs units of work. */
public class ObjectDbContext implements AutoCloseable {
    private final EntityManagerFactory emf;

    public ObjectDbContext(String databasePath) {
        try {
            emf = Persistence.createEntityManagerFactory(databasePath,
                    Map.of("jakarta.persistence.provider", "com.objectdb.jpa.Provider"));
        } catch (RuntimeException e) {
            throw new RepositoryException("cannot open database " + databasePath, e);
        }
    }
    public <T> T read(Function<EntityManager, T> work) {
        EntityManager em = open();
        try { return work.apply(em); }
        catch (RuntimeException e) { throw translate(e); }
        finally { em.close(); }
    }
    public void write(Consumer<EntityManager> work) {
        EntityManager em = open();
        try {
            em.getTransaction().begin();
            work.accept(em);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw translate(e);
        } finally { em.close(); }
    }
    private EntityManager open() {
        if (!emf.isOpen()) throw new RepositoryException("database connection is closed");
        return emf.createEntityManager();
    }
    private RuntimeException translate(RuntimeException e) {
        return (e instanceof RepositoryException) ? e : new RepositoryException(e.getMessage(), e);
    }
    @Override public void close() { if (emf.isOpen()) emf.close(); }
}
