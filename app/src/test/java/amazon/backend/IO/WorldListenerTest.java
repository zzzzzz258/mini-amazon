package amazon.backend.IO;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.SingletonSessionFactory;
import amazon.backend.manager.AckManager;
import amazon.backend.manager.LogisticsManager;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import amazon.backend.model.WorldMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class WorldListenerTest {

  Logger logger = LogManager.getLogger(this.getClass());

  @Test
  void run() throws IOException, InterruptedException {
    String ip = "vcm-25372.vm.duke.edu";
    int portu = 12345;
    int port = 23456;
    long worldId = 12;

    logger.info("***************************test_puchase_with_world starts***************************");

    UpsWorldIO upsWorldIO = new UpsWorldIO(ip, portu);
    upsWorldIO.sendConnect();
    worldId = upsWorldIO.recvConnected();


    WorldIO worldIO = WorldIO.newInstance(ip, port, worldId);

    WorldListener worldListener = new WorldListener(worldIO
            , AckManager.newInstance(SingletonSessionFactory.getSessionFactory())
            , LogisticsManager.newInstance(SingletonSessionFactory.getSessionFactory()));
    Thread thread = new Thread(worldListener);
    thread.setName("World Listener Thread");
    thread.start();

    WorldMessageDao worldMessageDao = new WorldMessageDao();
    PackageDao packageDao = new PackageDao();
    ProductDao productDao = new ProductDao();

    for (int i = 0; i < 10; i++) {
      Thread.sleep(1000);
      long packageId = packageDao.addOne(new Package(worldIO.warehouseIds.get(0), i, 1, 1));
      Product product = new Product(packageId, 99, "a good product", 2);
      productDao.addOne(product);
      long seqNum = worldIO.sendAPurchaseMore(worldIO.warehouseIds.get(0), List.of(product));
      productDao.setBuySeq(packageId, seqNum);
      worldMessageDao.addOne(new WorldMessage(seqNum));
    }

    while (true);

  }
}