package amazon.backend.IO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.manager.AckManager;
import amazon.backend.model.Product;
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
    int port = 12345;
    int portu = 23456;
    int worldId = 12;

    logger.info("***************************test_puchase_with_world starts***************************");

    UpsWorldIO upsWorldIO = new UpsWorldIO(ip, portu);
    upsWorldIO.sendConnect(worldId);
    upsWorldIO.recvConnected();


    WorldIO worldIO = WorldIO.newInstance(ip, port, worldId);

    WorldListener worldListener = new WorldListener(worldIO, AckManager.newInstance(SingletonSessionFactory.getSessionFactory()));
    Thread thread = new Thread(worldListener);
    thread.setName("World Listener Thread");
    thread.start();

    for (int i = 0; i < 10; i++) {
      Thread.sleep(1000);
      worldIO.sendAPurchaseMore(worldIO.warehouseIds.get(i%2), List.of(new Product(99, "a good product", 2)));
    }

    while (true);

  }
}