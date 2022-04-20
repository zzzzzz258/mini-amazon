package amazon.backend.service;

import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.WorldMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AckWorldCommandServiceTest {

    @Test
    void run() {
        WorldMessageDao worldMessageDao = new WorldMessageDao(SingletonSessionFactory.getSessionFactory());
        long seqNum = 156798234;
        if (worldMessageDao.getOne(seqNum) != null) {
            worldMessageDao.deleteOne(seqNum);
        }
        worldMessageDao.addOne(new WorldMessage(seqNum));
        assertFalse(worldMessageDao.getOne(seqNum).isAcked());

        AckWorldCommandService ackWorldCommandService = new AckWorldCommandService(seqNum);
        ackWorldCommandService.run();

        assertTrue(worldMessageDao.getOne(seqNum).isAcked());
    }
}