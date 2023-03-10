package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.IO.WebIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import amazon.backend.model.WorldMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.FrontBack;

import java.io.IOException;
import java.util.List;


public class DealWebOrderService implements Runnable {

    Logger logger = LogManager.getLogger();

    private FrontBack.FBMessage order;

    public DealWebOrderService(FrontBack.FBMessage order) {
        this.order = order;
    }

    /**
     * Deal with an order accepted from web service
     * add order(package) and product info to database
     * send response to web
     * request purchase from world
     */
    @Override
    public void run() {

      logger.info("Processing web order: " + order.getPid());
      
      PackageDao packageDao = new PackageDao();
      ProductDao productDao = new ProductDao();
      WorldMessageDao worldMessageDao = new WorldMessageDao();
      WorldIO worldIO = WorldIO.getInstance();

      Package pkg = new Package(getWarehouseId(order.getProducts().getIid())
              , order.getPid()
              , order.getX()
              , order.getY()
              , order.getUpsAccountName());
      long packageId = packageDao.addOne(pkg);
      FrontBack.Product frontProdcut = order.getProducts();
      if (frontProdcut == null) {
          logger.warn("No product in order from web, order id: " + order.getPid());
      }
      Product product = new Product(packageId
              ,frontProdcut.getIid()
              , frontProdcut.getDescription()
              , frontProdcut.getCount());
      productDao.addOne(product);

      // Send response to web
      WebIO webIO = WebIO.getInstance();
      webIO.sendStatus(pkg.getOrderId(), "Packing");

      // send purchase request to world
      try {
        long seqNum = worldIO.sendAPurchaseMore(pkg.getWarehouseId(), List.of(product));
          worldMessageDao.addOne(new WorldMessage(seqNum));
          productDao.setBuySeq(packageId, seqNum);          
      } catch (IOException e) {
          e.printStackTrace();
      }
    }

    public int getWarehouseId(long productId) {
      int index = (int) (productId % WorldIO.getInstance().warehouseIds.size());
      return WorldIO.getInstance().warehouseIds.get(index);
    }
}
