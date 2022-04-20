package amazon.backend.IO;

import amazon.backend.manager.LogisticsManager;
import com.google.protobuf.CodedOutputStream;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import protobuf.FrontBack;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebListenerTest {

    @Mock
    LogisticsManager logisticsManager = mock(LogisticsManager.class);

    @Test
    void getInstance() {
        assertNull(WebListener.getInstance());
    }

    @Test
    void run() throws IOException {
        Thread webT = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("0.0.0.0", 3579);

                    Thread.sleep(50);

                    FrontBack.Product product = FrontBack.Product.newBuilder()
                            .setIid(1).setCount(2).setDescription("3").build();
                    FrontBack.FBMessage order = FrontBack.FBMessage.newBuilder()
                            .setPid(1).setProducts(product).setX(3).setY(4).build();
                    byte[] data = order.toByteArray();
                    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(socket.getOutputStream());
                    codedOutputStream.writeUInt32NoTag(data.length);
                    codedOutputStream.writeRawBytes(data);
                    codedOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        WebIO mockWebIO = WebIO.newInstance(3579);
        WebListener webListener = WebListener.newInstance(mockWebIO, logisticsManager);

        webListener.run();

        

    }
}