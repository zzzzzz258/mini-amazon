package amazon.backend.manager;

import amazon.backend.service.AckWorldCommandService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    Logger logger = LogManager.getLogger();

    ThreadPoolExecutor threadPoolExecutor;

    private AckManager() {
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        logger.info("AckManager Instance contructed");
    }

    public static synchronized AckManager getInstance() {
        return INSTANCE;
    }

    public static synchronized AckManager newInstance(SessionFactory sessionFactory) {
        INSTANCE = new AckManager();
        return INSTANCE;
    }

    /**
     * Do acknowledgement to given sequence
     * @param seq
     */
    public synchronized void doAck(long seq) {
        threadPoolExecutor.execute(new AckWorldCommandService(seq));
    }
}
