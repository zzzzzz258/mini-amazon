package amazon.backend.service;

import amazon.backend.IO.WorldIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.WorldAmazon;

public class ProductArrivedService implements Runnable {

  Logger logger = LogManager.getLogger();

  private WorldAmazon.APurchaseMore aPurchaseMore;

  public ProductArrivedService(WorldAmazon.APurchaseMore aPurchaseMore) {
    this.aPurchaseMore = aPurchaseMore;
  }


  @Override
  public void run() {
    logger.info("Processing response APurchaseMore\n" + aPurchaseMore);
    WorldIO worldIO = WorldIO.getInstance();

    // send ack back
    worldIO.sendAck(aPurchaseMore.getSeqnum());

    // TODO update databse, product arrived


    // TODO check if the package is full, ask for packing if so
  }
}
