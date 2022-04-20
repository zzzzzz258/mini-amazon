package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
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
        sessionFactory = SingletonSessionFactory.getSessionFactory();
    }

    @BeforeEach
    public void init_each() {
        worldMessageDao = new WorldMessageDao(sessionFactory);
    }

    @Test
    public void test_addOne_ackOne() {
        long seqNum = 10568832;
        if (worldMessageDao.getOne(seqNum) != null) {
            worldMessageDao.deleteOne(seqNum);
            assertNull(worldMessageDao.getOne(seqNum));
        }
        worldMessageDao.addOne(new WorldMessage(seqNum));
        WorldMessage message = worldMessageDao.getOne(seqNum);
        assertNotNull(message);
        assertFalse(message.isAcked());
        worldMessageDao.ackOne(seqNum);
        message = worldMessageDao.getOne(seqNum);
        assertTrue(message.isAcked());
    }
}