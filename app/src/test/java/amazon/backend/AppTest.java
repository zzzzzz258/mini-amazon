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
import com.google.protobuf.CodedOutputStream;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import protobuf.FrontBack;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class AppTest {
    public WorldIO amazonIO;
    public UpsWorldIO upsWorldIO;
    public WorldOutputListener worldOutputListener;
    public SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
    public AckManager ackManager = AckManager.newInstance(sessionFactory);

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
        amazonIO = WorldIO.newInstance(ip, amazonPort, worldId);

        worldOutputListener = WorldOutputListener.newInstance(WorldIO.getInstance(), AckManager.getInstance());
    }


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

    @Test
    public void test() throws IOException {
        Thread webThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Socket socket = new Socket("0.0.0.0", 2222);
                    FrontBack.Product product = FrontBack.Product.newBuilder()
                            .setIid(1).setCount(2).setDescription("3").build();
                    FrontBack.FBMessage order = FrontBack.FBMessage.newBuilder()
                            .setPid(1).setProducts(product).setX(3).setY(4).build();
                    byte[] data = order.toByteArray();
                    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(socket.getOutputStream());
                    codedOutputStream.writeUInt32NoTag(data.length);
                    codedOutputStream.writeRawBytes(data);
                    codedOutputStream.flush();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        App app = new App();
        app.start();

    }
}
