package amazon.backend.manager;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.WorldMessage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

class AckManagerTest {
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void init() {
        sessionFactory = SingletonSessionFactory.getSessionFactory();
    }

    @Test
    public void test_hibernate() {

    }

    @Test
    public void test_ThreadPoolExecutor() {

    }
}