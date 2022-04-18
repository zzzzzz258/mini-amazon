package amazon.backend.DAO;

import amazon.backend.model.WorldMessage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldMessageDaoTest {
    private static SessionFactory sessionFactory;
    private WorldMessageDao worldMessageDao;

    @BeforeAll
    public static void init() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @BeforeEach
    public void init_each() {
        worldMessageDao = new WorldMessageDao(sessionFactory);
    }

    @Test
    public void test_addOne() {
        worldMessageDao.addOne(new WorldMessage(99));
        worldMessageDao.ackOne(99);
    }
}