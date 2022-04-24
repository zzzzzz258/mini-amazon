package amazon.backend.manager;

import amazon.backend.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.AmazonUps;
import protobuf.FrontBack;
import org.hibernate.SessionFactory;
import protobuf.WorldAmazon;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class LogisticsManager {

    private static LogisticsManager INSTANCE;
    Logger logger = LogManager.getLogger();

    ThreadPoolExecutor orderConfirmedPool;
    ThreadPoolExecutor productArrivedPool;
    ThreadPoolExecutor packagePackedPool;
    ThreadPoolExecutor truckReadyPool;
    ThreadPoolExecutor packageLoadedPool;

    private LogisticsManager() {
        orderConfirmedPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        productArrivedPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        packagePackedPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        truckReadyPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        packageLoadedPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        logger.info("Logistics manager instance constructed");
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
        logger.info("Logistics Manager gets FB message: " + order.getPid());
        orderConfirmedPool.execute(new DealWebOrderService(order));
    }

    public synchronized void purchaseArrived(WorldAmazon.APurchaseMore aPurchaseMore) {
        logger.info("Logistics manager gets APurchaseMore: " + aPurchaseMore.getSeqnum());
        productArrivedPool.execute(new ProductArrivedService(aPurchaseMore));
    }

    public synchronized void packagePacked(WorldAmazon.APacked aPacked) {
        logger.info("Logistics manager get APacked: " + aPacked.getSeqnum());
        packagePackedPool.execute(new PackagePackedService(aPacked));
    }

    public synchronized void truckReady(AmazonUps.UAReadyForPickup uaReadyForPickup) {
        logger.info("Logistics manager get UAReadyForPickup: " + uaReadyForPickup.getSeqnum());
        truckReadyPool.execute(new TruckReadyService(uaReadyForPickup));
    }

    public synchronized void packageLoaded(WorldAmazon.ALoaded aLoaded) {
        logger.info("Logistics manager get ALoaded: " + aLoaded.getSeqnum());
        packageLoadedPool.execute(new PackageLoadedService(aLoaded));
    }
}
