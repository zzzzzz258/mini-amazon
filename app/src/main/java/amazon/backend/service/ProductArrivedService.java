package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
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

    for (WorldAmazon.AProduct aProduct: aPurchaseMore.getThingsList()) {
      // update databse, product arrived
      ProductDao productDao = new ProductDao();
      long packageId = productDao.productBought(aProduct.getId(), aProduct.getCount());

      // TODO check if the package is full, ask for packing if so

    }
  }

//  public boolean checkIfPackageReady() {
//    PackageDao packageDao = new PackageDao();
//
//  }
}
