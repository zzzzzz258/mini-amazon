package amazon.backend.service;

import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.model.WorldMessage;
import org.hibernate.SessionFactory;

public class AckWorldCommandService implements Runnable{

    private long seqNum;

    public AckWorldCommandService(long seqNum) {
        this.seqNum = seqNum;
    }

    /**
     * Acknowledge a command sent to world
     */
    @Override
    public void run() {
        WorldMessageDao worldMessageDao = new WorldMessageDao();
        worldMessageDao.ackOne(seqNum);
    }
}
