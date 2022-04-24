package amazon.backend.manager;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import amazon.backend.model.WorldMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;
import protobuf.WorldAmazon;

import java.io.IOException;
import java.util.List;

public class ResendManager implements Runnable{
  Logger logger = LogManager.getLogger();
  private static ResendManager INSTANCE;

  private ResendManager() {
  }

  public static ResendManager getInstance() {
    return INSTANCE;
  }

  public static ResendManager newInstance() {
    INSTANCE = new ResendManager();
    return INSTANCE;
  }


  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      long timeThreshold = System.currentTimeMillis() - 1000;
      WorldMessageDao worldMessageDao = new WorldMessageDao();

      List<WorldMessage> worldMessageList = worldMessageDao.getUnackedList(timeThreshold);
      for (WorldMessage worldMessage : worldMessageList) {
        boolean flag = resendToWorld(worldMessage.getSequenceNum());
        if (flag) {
          worldMessageDao.setSentTime(worldMessage.getSequenceNum(), System.currentTimeMillis());
        }
      }
    }
  }

  public boolean resendToWorld(long seq) {
    PackageDao packageDao = new PackageDao();
    ProductDao productDao = new ProductDao();

    Package pkgP = packageDao.getOneByPackSeq(seq);
    Package pkgL = packageDao.getOneByLoadSeq(seq);
    List<Product> productList = productDao.getListByBuySeq(seq);

    if (productList != null && productList.size() > 0) {
      resendAPurchaseMore(productList, seq);
    }
    else if (pkgP != null) {
      resendAPack(pkgP, seq);
    }
    else if(pkgL != null) {
      resendALoad(pkgL, seq);
    }
    else {
      logger.error("Resend fails, cannot find any record for seqNum" + seq);
      return false;
    }
    return true;
  }

  private void resendAPack(Package pkg, long seq) {
    ProductDao productDao = new ProductDao();
    List<Product> products = productDao.getPackageProducts(pkg.getPackageId());
    WorldIO worldIO = WorldIO.getInstance();
    logger.info("Resend APack to world: " + seq);
    System.out.println("Resend APack to world: " + seq);
    worldIO.sendAPack(pkg.getWarehouseId(), products, pkg.getPackageId(), seq);
  }

  private void resendALoad(Package pkg, long seq) {
    WorldIO worldIO = WorldIO.getInstance();
    logger.info("Resend ALoad to world: " + seq);
    System.out.println("Resend Aload to world: " + seq);
    worldIO.sendAPutOnTruck(pkg.getWarehouseId(), pkg.getTruckId(), pkg.getPackageId(), seq);
  }

  public void resendAPurchaseMore(List<Product> productList, long seq) {
    long packageId = productList.get(0).getPackageId();
    PackageDao packageDao = new PackageDao();
    Package pkg = packageDao.getOne(packageId);

    WorldIO worldIO = WorldIO.getInstance();
    logger.info("Resend APurchaseMore to world: " + seq);
    System.out.println("Resend APurchaseMore to world: " + seq);
    worldIO.sendAPurchaseMore(pkg.getWarehouseId(), productList, seq);
  }

}
