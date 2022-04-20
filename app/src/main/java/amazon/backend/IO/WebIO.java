package amazon.backend.IO;

import protobuf.FrontBack;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WebIO {
    private static WebIO INSTANCE;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    /**
     * This constructor depends on a single specific Web service to connect
     * @param port
     * @throws IOException
     */
    private WebIO(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        System.out.println("Web connected");
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    public static synchronized WebIO getInstance() throws IllegalStateException {
        return INSTANCE;
    }

    public static synchronized WebIO newInstance(int port) throws IOException {
        INSTANCE = new WebIO(port);
        return INSTANCE;
    }

    /**
     * This method should be used for testing only
     * @param webIO
     * @return
     */
    public static WebIO newInstance(WebIO webIO) {
        INSTANCE = webIO;
        return INSTANCE;
    }

    public void sendStatus(int orderId, String status) throws IOException {
        sendBFMessage(orderId, status, null, null);
    }

    public void sendTrackingNum(int orderId, String trackingNum) throws IOException {
        sendBFMessage(orderId, null, trackingNum, null);
    }

    public void sendIsMatched(int orderId, boolean isMatched) throws IOException {
        sendBFMessage(orderId, null, null, isMatched);
    }

    public void sendBFMessage(int orderId, String status, String trackingNum, Boolean isMatched) throws IOException {
        FrontBack.BFMessage.Builder builder = FrontBack.BFMessage.newBuilder();
        builder.setPid(orderId);
        if (status != null) {
            builder.setStatus(status);
        }
        if (trackingNum != null) builder.setTrackingNum(trackingNum);
        if (isMatched != null) builder.setIsMatched(isMatched);
        sendToWeb(builder.build().toByteArray());
    }

    /**
     * Method to send message to world
     * @param data sending data in byte array
     * @throws IOException
     */
    public synchronized void sendToWeb(byte[] data) throws IOException {
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
    public <T extends GeneratedMessageV3.Builder<?>> boolean receiveFromWeb(T responseBuilder) throws IOException {
        if (inputStream.available()>0 ) {
          CodedInputStream cis = CodedInputStream.newInstance(inputStream);
          int size = cis.readRawVarint32();
        System.out.println(size);
        int oldLimit = cis.pushLimit(size);
        responseBuilder.mergeFrom(cis);
        cis.popLimit(oldLimit);
        System.out.println("Receive from web finish");
        return true;
        }
        return false;
    }
}
