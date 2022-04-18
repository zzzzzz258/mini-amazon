package amazon.backend.manager;

import amazon.backend.model.WorldMessage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AckManagerTest {
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void init() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Test
    public void test_hibernate() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            WorldMessage worldMessage = new WorldMessage(1);
            session.save(worldMessage);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}