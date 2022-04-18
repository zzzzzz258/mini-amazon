/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package amazon.backend;

import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.IO.WorldIO;
import amazon.backend.IO.WorldOutputListener;
import amazon.backend.manager.AckManager;
import amazon.backend.model.Product;
import amazon.backend.model.WorldMessage;
import amazon.backend.simpleups.UpsWorldIO;
import amazon.backend.simpleups.WorldUps;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class AppTest {
    public WorldIO amazonIO;
    public UpsWorldIO upsWorldIO;
    public WorldOutputListener worldOutputListener;
    public SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
    public AckManager ackManager = AckManager.getInstance(sessionFactory);

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

        System.out.println(uConnected);

        // connect to the world using given id with my amazon
        amazonIO = new WorldIO(ip, amazonPort, worldId);

        worldOutputListener = new WorldOutputListener(amazonIO, ackManager);
    }

    @Test
    public void test_purchase() throws IOException {
        test_connect_world();
        // send purchase to world
        List<Product> productList = new ArrayList<Product>();
        productList.add(new Product(1, "holy shit", 2));
        // TODO move insert world message to object later
        WorldMessageDao dao = new WorldMessageDao(sessionFactory);
        dao.addOne(new WorldMessage(amazonIO.sendAPurchaseMore(1, productList)));
        // receive from world
        while (true) {
            worldOutputListener.receive();
        }
    }
}
