package amazon.backend.manager;

import amazon.backend.service.AckWorldCommandService;
import org.hibernate.SessionFactory;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class to manage the ack message from world by managing a thread pool.
 * i.e. Offer new ack seq number into queue, then get it from queue and update in database
 * Following singleton design pattern
 */
public class AckManager {

    private static AckManager INSTANCE;

    private SessionFactory sessionFactory;
    private final Object lock = new Object();

    ThreadPoolExecutor threadPoolExecutor;

    private AckManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
    }

    public static synchronized AckManager getInstance(SessionFactory sessionFactory) {
        if (INSTANCE == null) {
            INSTANCE = new AckManager(sessionFactory);
        }
        return INSTANCE;
    }

    /**
     * Do acknowledgement to given sequence
     * @param seq
     */
    public synchronized void doAck(long seq) {
        threadPoolExecutor.execute(new AckWorldCommandService(sessionFactory, seq));
    }

}
