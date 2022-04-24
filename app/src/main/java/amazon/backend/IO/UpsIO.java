package amazon.backend.IO;

import amazon.backend.DAO.WarehouseDao;
import amazon.backend.model.Warehouse;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;
import protobuf.AmazonUps;
import protobuf.WorldAmazon;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UpsIO {
  private static UpsIO INSTANCE;
  Logger logger = LogManager.getLogger();

  private Socket socket;
  private OutputStream outputStream;
  private InputStream inputStream;
  private boolean isBufferEmpty;
  private long seqNum;


  private AmazonUps.AUCommand.Builder bufferBuilder;
  Lock bufferLock = new ReentrantLock();

  private UpsIO(int port) throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    socket = serverSocket.accept();
    logger.info("Ups connected, port:" + socket.getPort());
    outputStream = socket.getOutputStream();
    inputStream = socket.getInputStream();
    bufferBuilder = AmazonUps.AUCommand.newBuilder();
    isBufferEmpty = true;
    seqNum = 1;
  }

  public static synchronized UpsIO getInstance() {
    return INSTANCE;
  }

  public static synchronized UpsIO newInstance(int port) throws IOException {
    INSTANCE = new UpsIO(port);
    return INSTANCE;
  }

  public boolean isBufferEmpty() {
    return isBufferEmpty;
  }

  public void bufferNotEmpty() {
    isBufferEmpty = false;
  }

  public void bufferEmpty() {
    isBufferEmpty = true;
  }


  public long receiveConnectFromUps() throws IOException {
    // wait for response
    AmazonUps.UAConnect.Builder connectedBuilder = AmazonUps.UAConnect.newBuilder();
    receiveFromUps(connectedBuilder);
    AmazonUps.UAConnect connected = connectedBuilder.build();
    logger.info("Receive connect from ups:\n" + connected);
    return connected.getWorldid();
  }

  public void sendConnectToUps() throws IOException {
    // send connect request
    AmazonUps.AUConnected connect = createAConnect();
    sendToUps(connect.toByteArray());
    logger.info("Send connected to ups:\n"+connect);
    socket.setSoTimeout(50);
  }

  private AmazonUps.AUConnected createAConnect() {
    return AmazonUps.AUConnected.newBuilder().setSeqnum(getSeqNum()).setWorldConnectionStatus(true).build();
  }

  public void sendAURequestPickUp(WorldAmazon.APack aPack, String upsAccount, int x, int y) {
    AmazonUps.AUPack.Builder auPackBuilder = AmazonUps.AUPack.newBuilder().setDestx(x).setDesty(y).setPackage(aPack);
    if (upsAccount != null) {
      auPackBuilder.setUpsAccount(upsAccount);
    }
    AmazonUps.AURequestPickup auRequestPickup = AmazonUps.AURequestPickup.newBuilder().setPack(auPackBuilder.build())
            .setSeqnum(getSeqNum()).build();

    try {
      if (bufferLock.tryLock(10, TimeUnit.SECONDS)) {
        try {
          bufferBuilder.addPickupRequest(auRequestPickup);
          logger.info("Add new AURequestPickUp to BufferBuilder:\n" + auRequestPickup);
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
      logger.error("InterruptedException in sendAURequestPickUp:\n" + e.getStackTrace());
    }
  }

  public void sendAUReadyForDelivery(int truckId) {
    AmazonUps.AUReadyForDelivery.Builder builder = AmazonUps.AUReadyForDelivery.newBuilder();
    AmazonUps.AUReadyForDelivery auReadyForDelivery = builder.setTruckid(truckId).setSeqnum(getSeqNum()).build();

    try {
      if (bufferLock.tryLock(10, TimeUnit.SECONDS)) {
        try {
          bufferBuilder.addDeliveryReady(auReadyForDelivery);
          logger.info("Add new AUDeliveryReady to BufferBuilder:\n" + auReadyForDelivery);
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
      logger.error("InterruptedException in sendAUDeliveryReady:\n" + e.getStackTrace());
    }
  }

  public void sendAck(long num) {
    try {
      if (bufferLock.tryLock(10, TimeUnit.SECONDS)) {
        try {
          bufferBuilder.addAcks(num);
          logger.info("Add new ack to send to ups buffer: " + num);
          bufferNotEmpty();
        }
        finally {
          bufferLock.unlock();
        }
      } else {
        logger.fatal("Bad design: cannot send Ack due to lock");
      }
    } catch (InterruptedException e) {
      logger.error("InterruptedException in sendAck:\n" + e.getStackTrace());
    }
  }

  /**
   * Send command buffer to world
   * @return
   * @throws IOException
   */
  public void sendBufferToUps() throws IOException {
    try {
      if (bufferLock.tryLock(1, TimeUnit.SECONDS)) {
        try {
          AmazonUps.AUCommand auCommand = bufferBuilder.build();
          logger.info("Send AUCommand to world:\n" + auCommand);
          sendToUps(auCommand.toByteArray());
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
  public void sendToUps(byte[] data) throws IOException {
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
  public <T extends GeneratedMessageV3.Builder<?>> void receiveFromUps(T responseBuilder) throws IOException, SocketTimeoutException {
    synchronized (this) {
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
