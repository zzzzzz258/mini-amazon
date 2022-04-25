package amazon.backend.IO;

import amazon.backend.manager.LogisticsManager;
import amazon.backend.manager.StatusManager;
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
  StatusManager statusManager;

  private UpsListener(UpsIO upsIO, LogisticsManager logisticsManager, StatusManager statusManager) {
    this.upsIO = upsIO;
    this.logisticsManager = logisticsManager;
    this.statusManager = StatusManager.getInstance();
    logger.info("Ups listener constructed");
  }

  public static UpsListener getInstance() {
    return INSTANCE;
  }

  public static synchronized UpsListener newInstance(UpsIO upsIO
          , LogisticsManager logisticsManager
          , StatusManager statusManager) {
    INSTANCE = new UpsListener(upsIO, logisticsManager, statusManager);
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
    dispatchUAIsAssociated(uaCommand.getLinkResultList());
    printErrors(uaCommand.getErrorList());
  }

  private void dispatchUAReadyForPickup(List<UAReadyForPickup> readyForPickupList) {
    readyForPickupList.stream().forEach(uaReadyForPickup -> {
      logisticsManager.truckReady(uaReadyForPickup);
    });
  }

  private void dispatchUAPackageDelivered(List<UAPackageDelivered> uaPackageDeliveredList) {
    uaPackageDeliveredList.stream().forEach(uaPackageDelivered -> {
      statusManager.packageDelivered(uaPackageDelivered);
    });
  }

  private void dispatchUAIsAssociated(List<UAIsAssociated> uaIsAssociateds) {
    uaIsAssociateds.stream().forEach(uaPackageDelivered -> {
      statusManager.updateIsMatched(uaPackageDelivered);
    });
  }

  private void printErrors(List<Err> errs) {
    errs.stream().forEach(err -> logger.error("Error from ups:\n" + err));
  }

  @Override
  public void run() {
    logger.info("UpsListener starts running");
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
