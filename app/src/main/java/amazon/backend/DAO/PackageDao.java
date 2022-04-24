package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.Package;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;

public class PackageDao {
    Logger logger = LogManager.getLogger();
    private SessionFactory factory;

    public PackageDao() {
        factory = SingletonSessionFactory.getSessionFactory();
    }

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

    public int getWarehouseId(long id) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Package pkg = session.get(Package.class, id);
        if (pkg == null) {
            logger.fatal("Logic error, get warehouse id returns null");
        }

        session.close();

        return pkg.getWarehouseId();
    }

    public void setPackSeq(long id, long seqNum) {
        while (true) {
            Session session = factory.openSession();
            try {
                Transaction transaction = session.beginTransaction();

                Package pkg = session.get(Package.class, id);
                if (pkg == null) {
                    logger.fatal("Logic error, get warehouse id returns null");
                }
                pkg.setPackSeq(seqNum);
                session.merge(pkg);
                transaction.commit();

                session.close();
                return;
            } catch (Exception e) {
                logger.warn(e.getMessage());
            } finally {
                session.close();
            }
        }
    }

    public void setPacked(long id) {
        while (true) {
            Session session = factory.openSession();
            try {
                Transaction transaction = session.beginTransaction();

                Package pkg = session.get(Package.class, id);
                if (pkg == null) {
                    logger.fatal("Logic error, get warehouse id returns null");
                }
                pkg.setPacked(true);
                session.merge(pkg);

                transaction.commit();
                session.close();
                return;
            } catch (Exception e) {
                logger.warn(e.getMessage());
            } finally {
                session.close();
            }
        }
    }

    public int getOrderId(long packageId) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        Package pkg = session.get(Package.class, packageId);
        session.close();
        return pkg.getOrderId();
    }
}
