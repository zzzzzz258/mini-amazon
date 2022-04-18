package amazon.backend.IO;

import amazon.backend.model.Product;
import amazon.backend.simpleups.UpsWorldIO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amazon.backend.simpleups.WorldUps;

class WorldIOTest {

    public WorldIO amazonIO;
    public UpsWorldIO upsWorldIO;

    public void test_connect_world() throws IOException {
        String ip = "vcm-25372.vm.duke.edu";
        int upsPort = 23456;
        int amazonPort = 12345;
        int worldId = 1;

        // mock a ups to connect to a world
        upsWorldIO = new UpsWorldIO(ip, upsPort);
        WorldUps.UConnect uConnect =
                WorldUps.UConnect.newBuilder()
                        .setIsAmazon(false)
                        .setWorldid(worldId)
                        .build();

        upsWorldIO.sendToWorld(uConnect.toByteArray());

        // read result
        WorldUps.UConnected.Builder builder = WorldUps.UConnected.newBuilder();
        upsWorldIO.receiveFromWorld(builder);
        WorldUps.UConnected uConnected = builder.build();

        System.out.println(uConnected.getResult());
        System.out.println(uConnected.getWorldid());

        // connect to the world using given id with my amazon
        amazonIO = new WorldIO(ip, amazonPort, worldId);
    }

    @Test
    public void test_send_APurchaseMore() throws IOException {
        test_connect_world();
        List<Product> productList = new ArrayList<Product>();
        productList.add(new Product(1, "holy shit", 2));
        amazonIO.sendAPurchaseMore(1, productList);
    }
}