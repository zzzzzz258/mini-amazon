package amazon.backend.IO;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WebIO {

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    /**
     * This constructor depends on a single specific Web service to connect
     * @param port
     * @throws IOException
     */
    public WebIO(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        serverSocket.close();
    }

    // TODO send response to web service

    /**
     * Method to send message to world
     * @param data sending data in byte array
     * @throws IOException
     */
    public synchronized void sendToWorld(byte[] data) throws IOException {
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
    public synchronized <T extends GeneratedMessageV3.Builder<?>> void receiveFromWorld(T responseBuilder) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(inputStream);
        int size = cis.readRawVarint32();
        int oldLimit = cis.pushLimit(size);
        responseBuilder.mergeFrom(cis);
        cis.popLimit(oldLimit);
    }
}
