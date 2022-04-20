package amazon.backend.manager;

import amazon.backend.protobuf.FrontBack;
import amazon.backend.service.DealWebOrderService;
import org.hibernate.SessionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class LogisticsManager {

    private static LogisticsManager INSTANCE;

    ThreadPoolExecutor orderConfirmedPool;

    private LogisticsManager() {
        orderConfirmedPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
    }

    /**
     * Method to get the current singleton instance
     * @return instance, null if newInstance has never been called
     */
    public static synchronized LogisticsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Static method to create a new instance
     * @param sessionFactory
     * @return
     */
    public static synchronized LogisticsManager newInstance(SessionFactory sessionFactory) {
        INSTANCE = new LogisticsManager();
        return INSTANCE;
    }

    public synchronized void confirmOrder(FrontBack.FBMessage order) {
      System.out.println("Manager calls confirm Order");
        orderConfirmedPool.execute(new DealWebOrderService(order));
    }
}
