package amazon.backend.IO;

import amazon.backend.DAO.*;
import amazon.backend.DAO.WarehouseDao;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import amazon.backend.model.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;
import protobuf.WorldAmazon;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class WorldIO {
    private static WorldIO INSTANCE;
    Logger logger = LogManager.getLogger();

    public List<Integer> warehouseIds = new ArrayList<>();

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private long seqNum;
    private boolean isBufferEmpty;

    private WorldAmazon.ACommands.Builder bufferBuilder;
    Lock bufferLock = new ReentrantLock();

    /**
     * The warehosue positions to initialize
     */
    private final List<int[]> warehousePos = List.of(new int[] {1,1});

    public boolean isBufferEmpty() {
        return isBufferEmpty;
    }

    public void bufferNotEmpty() {
        isBufferEmpty = false;
    }

    public void bufferEmpty() {
        isBufferEmpty = true;
    }

    private WorldIO(String ip, int port, long worldId) throws IOException {
        socket = new Socket(ip, port);
        logger.info("World connected in port: " + socket.getPort());
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        connectToWorld(worldId);
        seqNum = 1;
        bufferBuilder = WorldAmazon.ACommands.newBuilder();
        isBufferEmpty = true;
        logger.info("Construct new WorldIO: "+ip+":"+port+" in world "+worldId);

        new ProductDao().clear();
        new PackageDao().clear();
        new WarehouseDao().clear();
        new WorldMessageDao().clear();

    }

    public static synchronized WorldIO getInstance() {
        return INSTANCE;
    }

    public static synchronized WorldIO newInstance(String ip, int port, long world) throws IOException {
        INSTANCE = new WorldIO(ip, port, world);
        return INSTANCE;
    }


    /**
     * Inject world into singleton, only used in test.
     * @param worldIO
     * @return
     */
    public static synchronized WorldIO newInstance(WorldIO worldIO) {
        INSTANCE = worldIO;
        return INSTANCE;
    }

    /**
     * Method to connect to World in the initialization phase of worldIO.
     * @param worldId
     * @throws IOException
     */
    private void connectToWorld(long worldId) throws IOException {
        // add init warehouses
        List<Warehouse> wareHouseList = new ArrayList<>();
        warehousePos.stream().forEach(pos -> {
            WarehouseDao warehouseDao = new WarehouseDao();
            int id = warehouseDao.addOne(new Warehouse(pos[0],pos[1]));
            warehouseIds.add(id);
            wareHouseList.add(new Warehouse(id, pos[0],pos[1]));
        });
        // send connect request
        WorldAmazon.AConnect connect = createAConnect(worldId, wareHouseList);
        sendToWorld(connect.toByteArray());
        logger.info("Send connect to world:\n"+connect);
        // wait for response
        WorldAmazon.AConnected.Builder connectedBuilder = WorldAmazon.AConnected.newBuilder();
        receiveFromWorld(connectedBuilder);
        WorldAmazon.AConnected connected = connectedBuilder.build();
        logger.info("Receive connected from world:\n" + connected);
        socket.setSoTimeout(50);
    }

    /**
     * Helper method to create an AConnect message
c     * @param worldId
     * @param wareHouseList
     * @return
     */
    private WorldAmazon.AConnect createAConnect(long worldId, List<Warehouse> wareHouseList) {
        WorldAmazon.AConnect.Builder builder = WorldAmazon.AConnect.newBuilder();
        builder.setWorldid(worldId).setIsAmazon(true);
        if (wareHouseList != null) {
            wareHouseList.stream().
                    forEach(wh -> builder.addInitwh(createAInitWarehouse(wh.getId(), wh.getX(), wh.getY())));
        }
        return builder.build();
    }

    /**
     * Helper method to create an AInitWarehouse
     * @param id
     * @param x
     * @param y
     * @return
     */
    private WorldAmazon.AInitWarehouse createAInitWarehouse(int id, int x, int y) {
        WorldAmazon.AInitWarehouse aInitWarehouse =
                WorldAmazon.AInitWarehouse.newBuilder()
                        .setId(id)
                        .setX(x)
                        .setY(y)
                        .build();
        return aInitWarehouse;
    }

    public long sendAPurchaseMore(int warehouseId, List<Product> products) throws IOException {
        long tSeqNum = getSeqNum();
        sendAPurchaseMore(warehouseId, products, tSeqNum);
        return tSeqNum;
    }

    /**
     * Method to send APurchaseMore to world
     * @param warehouseId
     * @param products
     */
    public void sendAPurchaseMore(int warehouseId, List<Product> products, long seqNum) {
        WorldAmazon.APurchaseMore.Builder builder = WorldAmazon.APurchaseMore.newBuilder().setWhnum(warehouseId);
        products.stream().forEach(p -> builder.addThings(createAProduct(p)));
        builder.setSeqnum(seqNum);

        try {
            if (bufferLock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    bufferBuilder.addBuy(builder.build());
                    logger.info("Add new APurchaseMore to BufferBuilder:\n" + builder.build());
                    bufferNotEmpty();
                }
                finally {
                    bufferLock.unlock();
                }
            }
            else {
                logger.fatal("Bad design");
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException in sendAPurchaseMore:\n" + e.getStackTrace());
        }
    }

    /**
     * Helper method to convert Prodct to GPB AProduct
     * @param product
     * @return
     */
    private WorldAmazon.AProduct createAProduct(Product product) {
        return WorldAmazon.AProduct.newBuilder()
                .setId(product.getProductId())
                .setDescription(product.getDescription())
                .setCount(product.getCount())
                .build();
    }

    public long sendAPack(int warehouseNum, List<Product> things, long shipId) {
        long tSeqNum = getSeqNum();
        sendAPack(warehouseNum, things, shipId, tSeqNum);

        return tSeqNum;
    }

    public void sendAPack(int warehouseNum, List<Product> things, long shipId, long seqNum) {
        WorldAmazon.APack.Builder builder = WorldAmazon.APack.newBuilder().setWhnum(warehouseNum).setShipid(shipId);
        things.stream().forEach(product -> {
            builder.addThings(WorldAmazon.AProduct.newBuilder()
                    .setId(product.getProductId())
                    .setDescription(product.getDescription())
                    .setCount(product.getCount()));
        });
        builder.setSeqnum(seqNum);

        try {
            if (bufferLock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    bufferBuilder.addTopack(builder.build());
                    logger.info("Add new APack to BufferBuilder:\n" + builder.build());
                    bufferNotEmpty();
                }
                finally {
                    bufferLock.unlock();
                }
            }
            else {
                logger.fatal("Bad design");
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException in sendAPack:\n" + e.getStackTrace());
        }
    }

    public long sendAPutOnTruck(int warehouseId, int truckId, long packageId) {
        long tSeqNum = getSeqNum();
        sendAPutOnTruck(warehouseId, truckId, packageId, tSeqNum);

        return tSeqNum;
    }

    public void sendAPutOnTruck(int warehouseId, int truckId, long packageId, long seqNum) {
        WorldAmazon.APutOnTruck.Builder builder = WorldAmazon.APutOnTruck.newBuilder()
                        .setWhnum(warehouseId).setTruckid(truckId).setShipid(packageId);
        builder.setSeqnum(seqNum);

        try {
            if (bufferLock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    bufferBuilder.addLoad(builder.build());
                    logger.info("Add new APutToTruck to BufferBuilder:\n" + builder.build());
                    bufferNotEmpty();
                }
                finally {
                    bufferLock.unlock();
                }
            }
            else {
                logger.fatal("Bad design");
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException in sendAPutOnTruck:\n" + e.getStackTrace());
        }
    }

    /**
     * Method to add ack to buffer
     * @param num
     */
    public void sendAck(long num) {
        try {
            if (bufferLock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    bufferBuilder.addAcks(num);
                    logger.info("Add new ack to send to world buffer: " + num);
                    bufferNotEmpty();
                }
                finally {
                    bufferLock.unlock();
                }
            } else {
                logger.fatal("Bad design: cannot send Ack due to lock");
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException in sendBufferToWorld:\n" + e.getStackTrace());
        }
    }

    /**
     * Send command buffer to world
     * @return
     * @throws IOException
     */
    public void sendBufferToWorld() throws IOException {
        try {
            if (bufferLock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    WorldAmazon.ACommands aCommands = bufferBuilder.setSimspeed(200).build();
                    logger.info("Send ACommands to world:\n" + aCommands);
                    sendToWorld(aCommands.toByteArray());
                    bufferBuilder.clear();
                    bufferEmpty();
                } catch (Exception e) {
                    logger.error("Unexpected exception in sendBufferToWorld:\n"+e.getStackTrace());
                } finally {
                    bufferLock.unlock();
                }
            } else {
                logger.warn("Send buffer to world fails due to lock");
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException in sendBufferToWorld:\n" + e.getStackTrace());
        }
    }

    /**
     * Method to send message to world
     * @param data sending data in byte array
     * @throws IOException
     */
    public void sendToWorld(byte[] data) throws IOException {
        synchronized (this) {
            CodedOutputStream cos = CodedOutputStream.newInstance(outputStream);
            cos.writeUInt32NoTag(data.length);
            cos.writeRawBytes(data);
            cos.flush();
        }
    }

    /**
     * Method to receive message from world。
     * @param responseBuilder is the message builder generated from world
     * @param <T>
     * @throws IOException
     */
    public <T extends GeneratedMessageV3.Builder<?>> void receiveFromWorld(T responseBuilder) throws IOException, SocketTimeoutException {
        synchronized (this) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            CodedInputStream cis = CodedInputStream.newInstance(inputStream);
            int size = cis.readRawVarint32();
            int oldLimit = cis.pushLimit(size);
            responseBuilder.mergeFrom(cis);
            cis.popLimit(oldLimit);
        }
    }

    /**
     * Synchronized method to update sequence number for each request.
     */
    private synchronized long getSeqNum() {
        return seqNum++;
    }


}
