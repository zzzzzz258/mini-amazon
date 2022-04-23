package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.Product;
import amazon.backend.model.WorldMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.WorldAmazon;

import java.util.List;

public class ProductArrivedService implements Runnable {

  Logger logger = LogManager.getLogger();

  private WorldAmazon.APurchaseMore aPurchaseMore;

  public ProductArrivedService(WorldAmazon.APurchaseMore aPurchaseMore) {
    this.aPurchaseMore = aPurchaseMore;
  }


  @Override
  public void run() {
    logger.info("Processing response APurchaseMore\n" + aPurchaseMore.getSeqnum());
    WorldIO worldIO = WorldIO.getInstance();

    // send ack back
    worldIO.sendAck(aPurchaseMore.getSeqnum());

    for (WorldAmazon.AProduct aProduct: aPurchaseMore.getThingsList()) {
      // update databse, product arrived
      ProductDao productDao = new ProductDao();
      PackageDao packageDao = new PackageDao();
      WorldMessageDao worldMessageDao = new WorldMessageDao();

      long packageId = productDao.productBought(aProduct.getId(), aProduct.getCount());

      // check if the package is full, ask for packing if so
      if (productDao.checkPackageReady(packageId)) {
        // get all products and send
        List<Product> products = productDao.getPackageProducts(packageId);
        int warehouseId = packageDao.getWarehouseId(packageId);
        long seqNum = worldIO.sendAPack(warehouseId, products, packageId);
        worldMessageDao.addOne(new WorldMessage(seqNum));
        packageDao.setPackSeq(packageId, seqNum);
      }

      // TODO send status packing to web
    }
  }

}
