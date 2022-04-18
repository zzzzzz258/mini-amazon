package amazon.backend.service;

import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.model.WorldMessage;
import org.hibernate.SessionFactory;

public class AckWorldCommandService implements Runnable{

    private SessionFactory sessionFactory;
    private long seqNum;

    public AckWorldCommandService(SessionFactory sessionFactory, long seqNum) {
        this.sessionFactory = sessionFactory;
        this.seqNum = seqNum;
    }

    /**
     * Acknowledge a command sent to world
     */
    @Override
    public void run() {
        WorldMessageDao worldMessageDao = new WorldMessageDao(sessionFactory);
        worldMessageDao.ackOne(seqNum);
    }
}
