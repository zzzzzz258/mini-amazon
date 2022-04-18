package amazon.backend.IO;

import amazon.backend.model.Product;
import amazon.backend.model.WareHouse;
import amazon.backend.protobuf.WorldAmazon;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class WorldIO {
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private long seqNum;

    /**
     * The warehosue positions to initialize
     */
    private final List<int[]> warehousePos = List.of(new int[] {1,1}, new int[] {551,550}, new int[] {651, 650}, new int[] {1128, 1112});

    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    public WorldIO(String ip, int port, int worldId) throws IOException {
        socket = new Socket(ip, port);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        connectToWorld(worldId);
        seqNum = 1;
    }

    /**
     * Method to connect to World in the initialization phase of worldIO.
     * @param worldId
     * @throws IOException
     */
    private void connectToWorld(int worldId) throws IOException {
        // add init warehouses
        List<WareHouse> wareHouseList =  warehousePos.stream().map(pair -> new WareHouse(warehousePos.indexOf(pair), pair[0], pair[1])).collect(Collectors.toList());
        // send connect request
        WorldAmazon.AConnect connect = createAConnect(worldId, wareHouseList);
        sendToWorld(connect.toByteArray());
        // wait for response
        WorldAmazon.AConnected.Builder connectedBuilder = WorldAmazon.AConnected.newBuilder();
        receiveFromWorld(connectedBuilder);
        WorldAmazon.AConnected connected = connectedBuilder.build();
        System.out.println(connected.getWorldid()+": "+connected.getResult());
    }

    /**
     * Helper method to create an AConnect message
     * @param worldId
     * @param wareHouseList
     * @return
     */
    private WorldAmazon.AConnect createAConnect(long worldId, List<WareHouse> wareHouseList) {
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

    /**
     * Method to send APurchaseMore to world
     * @param warehouseId
     * @param products
     */
    public long sendAPurchaseMore(int warehouseId, List<Product> products) throws IOException {
        WorldAmazon.APurchaseMore.Builder builder = WorldAmazon.APurchaseMore.newBuilder().setWhnum(warehouseId);
        products.stream().forEach(p -> builder.addThings(createAProduct(p)));
        long tSeqNum = getSeqNum();
        builder.setSeqnum(tSeqNum);
        WorldAmazon.ACommands aCommands = createACommands(List.of(builder.build()), null);
        System.out.println(aCommands);
        sendToWorld(aCommands.toByteArray());
        return tSeqNum;
    }

    /**
     * Helper method to convert Prodct to GPB AProduct
     * @param product
     * @return
     */
    private WorldAmazon.AProduct createAProduct(Product product) {
        return WorldAmazon.AProduct.newBuilder()
                .setId(product.getId())
                .setDescription(product.getDescription())
                .setCount(product.getCount())
                .build();
    }

    /**
     * Create an ACommands for aPurchaseMores only.
     * @param qPurchaseMores
     * @param simspeed
     * @return
     */
    private WorldAmazon.ACommands createACommands(List<WorldAmazon.APurchaseMore> qPurchaseMores,
                                                  Integer simspeed) {
        return createACommands(qPurchaseMores, null, null, null, simspeed, null, null);
    }

    /**
     * Create ACommands to send
     * @param aPurchaseMores
     * @param aPacks
     * @param aPutOnTrucks
     * @param aQuerys
     * @param simspeed
     * @param disconnect
     * @param acks
     * @return
     */
    private WorldAmazon.ACommands createACommands(List<WorldAmazon.APurchaseMore> aPurchaseMores,
                                                  List<WorldAmazon.APack> aPacks,
                                                  List<WorldAmazon.APutOnTruck> aPutOnTrucks,
                                                  List<WorldAmazon.AQuery> aQuerys,
                                                  Integer simspeed,
                                                  Boolean disconnect,
                                                  List<Long> acks) {
        WorldAmazon.ACommands.Builder builder = WorldAmazon.ACommands.newBuilder();
        if (aPurchaseMores != null) {
            aPurchaseMores.stream().forEach(ap -> builder.addBuy(ap));
        }
        if (aPacks != null) {
            aPacks.stream().forEach(ap -> builder.addTopack(ap));
        }
        if (aPutOnTrucks != null) {
            aPutOnTrucks.stream().forEach(ap -> builder.addLoad(ap));
        }
        if (aQuerys != null) {
            aQuerys.stream().forEach(aq -> builder.addQueries(aq));
        }
        if (simspeed != null) {
            builder.setSimspeed(simspeed);
        }
        if (disconnect != null) {
            builder.setDisconnect(disconnect);
        }
        if (acks != null) {
            acks.stream().forEach(ack -> builder.addAcks(ack));
        }
        return builder.build();
    }

    /**
     * Method to send message to world
     * @param data sending data in byte array
     * @throws IOException
     */
    public void sendToWorld(byte[] data) throws IOException {
        CodedOutputStream cos = CodedOutputStream.newInstance(outputStream);
        cos.writeUInt32NoTag(data.length);
        cos.writeRawBytes(data);
        cos.flush();
    }

    /**
     * Method to receive message from world。
     * @param responseBuilder is the message builder generated from world
     * @param <T>
     * @throws IOException
     */
    public <T extends GeneratedMessageV3.Builder<?>> void receiveFromWorld(T responseBuilder) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(inputStream);
        int size = cis.readRawVarint32();
        int oldLimit = cis.pushLimit(size);
        responseBuilder.mergeFrom(cis);
        cis.popLimit(oldLimit);
    }

    /**
     * Synchronized method to update sequence number for each request.
     */
    private synchronized long getSeqNum() {
        return seqNum++;
    }


}
