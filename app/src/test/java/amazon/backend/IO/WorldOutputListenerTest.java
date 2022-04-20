package amazon.backend.IO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.manager.AckManager;
import amazon.backend.model.Product;
import amazon.backend.simpleups.UpsWorldIO;
import amazon.backend.simpleups.WorldUps;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class WorldOutputListenerTest {

  @Test
  public void test_puchase_with_world() throws IOException, InterruptedException {
    String ip = "vcm-25372.vm.duke.edu";
    int port = 12345;
    int portu = 23456;
    int worldId = 13;

    UpsWorldIO upsWorldIO = new UpsWorldIO(ip, portu);
    upsWorldIO.sendConnect(worldId);
    upsWorldIO.recvConnected();


    WorldIO worldIO = WorldIO.newInstance(ip, port, worldId);

    WorldOutputListener worldOutputListener = new WorldOutputListener(worldIO, AckManager.newInstance(SingletonSessionFactory.getSessionFactory()));
    for (int i = 0; i < 4; i++) {
      Thread.sleep(4500);
      worldOutputListener.receive();
      worldIO.sendAPurchaseMore(worldIO.warehouseIds.get(i%2), List.of(new Product(99, "a good product", 2)));
    }


  }
}