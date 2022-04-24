package amazon.backend.IO;

import amazon.backend.manager.LogisticsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.AmazonUps;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import static protobuf.AmazonUps.*;

public class UpsListener implements Runnable {

  private static UpsListener INSTANCE;
  Logger logger = LogManager.getLogger();

  UpsIO upsIO;
  LogisticsManager logisticsManager;

  private UpsListener(UpsIO upsIO, LogisticsManager logisticsManager) {
    this.upsIO = upsIO;
    this.logisticsManager = logisticsManager;
    logger.info("Ups listener constructed");
  }

  public static UpsListener getInstance() {
    return INSTANCE;
  }

  public static synchronized UpsListener newInstance(UpsIO upsIO, LogisticsManager logisticsManager) {
    INSTANCE = newInstance(upsIO, logisticsManager);
    return INSTANCE;
  }

  public void receive() throws IOException, SocketTimeoutException{
    UACommand.Builder builder = UACommand.newBuilder();
    upsIO.receiveFromUps(builder);
    UACommand uaCommand = builder.build();
    logger.info("UACommand from ups: \n" + uaCommand);
    // dispatch
    dispatchUAReadyForPickup(uaCommand.getPickupReadyList());
    dispatchUAPackageDelivered(uaCommand.getPackageDeliveredList());
    printErrors(uaCommand.getErrorList());
  }

  private void dispatchUAReadyForPickup(List<UAReadyForPickup> readyForPickupList) {
    readyForPickupList.stream().forEach(uaReadyForPickup -> {
      // TODO
    });
  }

  private void dispatchUAPackageDelivered(List<UAPackageDelivered> uaPackageDeliveredList) {
    uaPackageDeliveredList.stream().forEach(uaPackageDelivered -> {

    });
    // TODO
  }

  private void printErrors(List<Err> errs) {
    errs.stream().forEach(err -> logger.error("Error from ups:\n" + err));
  }

  @Override
  public void run() {
    logger.info("WorldListener starts running");
    while (true) {
      try {
        receive();
      } catch (SocketTimeoutException e) {
        // send buffer if read nothing
        if (!upsIO.isBufferEmpty()) {
          try {
            upsIO.sendBufferToUps();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
