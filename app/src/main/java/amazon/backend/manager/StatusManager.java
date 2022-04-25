package amazon.backend.manager;

import amazon.backend.service.PackageDeliveredService;
import amazon.backend.service.UpdateIsMatchedService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.AmazonUps;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class StatusManager {
  private static StatusManager INSTANCE;
  Logger logger = LogManager.getLogger();

  ThreadPoolExecutor packageDeliveredPool;
  ThreadPoolExecutor updateTrackingNumPool;
  ThreadPoolExecutor updateIsMatchedPool;

  private StatusManager() {
    packageDeliveredPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
    updateTrackingNumPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    updateIsMatchedPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    logger.info("Status manager instance constructed");
  }

  public static synchronized StatusManager getInstance() {
    return INSTANCE;
  }

  public static synchronized StatusManager newInstance() {
    INSTANCE = new StatusManager();
    return INSTANCE;
  }

  public synchronized void packageDelivered(AmazonUps.UAPackageDelivered packageDelivered) {
    logger.info("Status manager gets packageDelivered: " + packageDelivered.getPackageid());
    packageDeliveredPool.execute(new PackageDeliveredService(packageDelivered));
  }


  public synchronized void updateIsMatched(AmazonUps.UAIsAssociated uaIsAssociated) {
    logger.info("Status manager gets UAIsAssociated: \n" + uaIsAssociated);
    updateIsMatchedPool.execute(new UpdateIsMatchedService(uaIsAssociated));
  }

}
