package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
import amazon.backend.IO.UpsIO;
import amazon.backend.IO.WebIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.WorldAmazon;

import java.io.IOException;
import java.util.List;

public class PackagePackedService implements Runnable {

  Logger logger = LogManager.getLogger();

  private WorldAmazon.APacked aPacked;

  public PackagePackedService(WorldAmazon.APacked aPacked) {
    this.aPacked = aPacked;
  }

  @Override
  public void run() {
    // send ACK back
    logger.info("Processing response APacked: " + aPacked.getSeqnum());
    WorldIO worldIO = WorldIO.getInstance();
    PackageDao packageDao = new PackageDao();
    ProductDao productDao = new ProductDao();

    worldIO.sendAck(aPacked.getSeqnum());

    // update databse, record packed
    packageDao.setPacked(aPacked.getShipid());

    // send to zz ready for shipment
    WebIO webIO = WebIO.getInstance();
    webIO.sendStatus(packageDao.getOrderId(aPacked.getShipid()), "Ready for shipment");

    // send to ups for pick our shit
    UpsIO upsIO = UpsIO.getInstance();
    Package pkg = packageDao.getOne(aPacked.getShipid());
    WorldAmazon.APack aPack = createAPack(pkg, productDao.getPackageProducts(pkg.getPackageId()));
    upsIO.sendAURequestPickUp(aPack, pkg.getUpsAccountName(), pkg.getX(), pkg.getY());
    

  }

  public WorldAmazon.APack createAPack(Package pkg, List<Product> productList) {
    WorldAmazon.APack.Builder builder = WorldAmazon.APack.newBuilder();
    builder.setWhnum(pkg.getWarehouseId()).setShipid(pkg.getPackageId()).setSeqnum(250);
    productList.parallelStream().forEach(product -> {
      WorldAmazon.AProduct aProduct = WorldAmazon.AProduct.newBuilder()
              .setId(product.getProductId())
              .setCount(product.getCount())
              .setDescription(product.getDescription())
              .build();
      builder.addThings(aProduct);
    });
    return builder.build();
  }
}
