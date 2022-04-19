package amazon.backend.DAO;

import amazon.backend.model.Package;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;

public class PackageDao {
    private SessionFactory factory;

    public PackageDao(SessionFactory factory) {
        this.factory = factory;
    }

    /**
     * Insert a new package
     * @param target
     */
    public long addOne(Package target) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        session.persist(target);
        session.flush();
        session.evict(target);

        transaction.commit();
        session.close();

        long newId = target.getPackageId();

        return newId;
    }

    /**
     * Get one package by id
     * @param id
     * @return
     */
    public Package getOne(long id) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Package p = session.get(Package.class, id);

        transaction.commit();
        session.close();

        return p;
    }

    /**
     * Delete one package row by its id
     * @param id
     */
    public void deleteOne(long id) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Package p = new Package(id);
        session.remove(p);

        transaction.commit();
        session.close();
    }
}
