package amazon.backend.IO;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.WorldUps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UpsWorldIO {
    Logger logger = LogManager.getLogger();

    public Socket socket;
    public OutputStream outputStream;
    public InputStream inputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public UpsWorldIO(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        logger.info("Mock Ups connected to World");
    }

    public void sendConnect(int worldId) throws IOException {
        WorldUps.UConnect uConnect = WorldUps.UConnect.newBuilder().setWorldid(worldId).setIsAmazon(false).build();
        sendToWorld(uConnect.toByteArray());
        logger.info("Mock ups send connect to world \n"+uConnect);
    }

    public void recvConnected() throws IOException {
        WorldUps.UConnected.Builder builder = WorldUps.UConnected.newBuilder();
        receiveFromWorld(builder);
        logger.info("Mock ups receive connected from world:\n" + builder.build());
    }

    public void sendToWorld(byte[] data) throws IOException {
        CodedOutputStream cos = CodedOutputStream.newInstance(outputStream);
        cos.writeUInt32NoTag(data.length);
        cos.writeRawBytes(data);
        cos.flush();
    }

    public <T extends GeneratedMessageV3.Builder<?>> void receiveFromWorld(T response) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(inputStream);
        int size = cis.readRawVarint32();
        int oldLimit = cis.pushLimit(size);
        response.mergeFrom(cis);
        cis.popLimit(oldLimit);
    }

}
