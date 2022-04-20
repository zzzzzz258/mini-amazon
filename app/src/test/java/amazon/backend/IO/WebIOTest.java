package amazon.backend.IO;

import amazon.backend.model.WorldMessage;
import com.google.protobuf.CodedOutputStream;
import org.junit.jupiter.api.Test;
import protobuf.FrontBack;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebIOTest {


    @Test
    void getInstance() {
        assertNull(WebIO.getInstance());
    }

    @Test
    void newInstance() throws IOException {
        final boolean[] connected = {false};
        Thread tClient = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("0.0.0.0", 7878);
                    connected[0] = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tClient.start();
        assertFalse(connected[0]);
        WebIO.newInstance(7878);
        assertTrue(connected[0]);

    }

    @Test
    void testNewInstance() {
        WebIO mockWebIO = mock(WebIO.class);
        assertSame(mockWebIO, WebIO.newInstance(mockWebIO));
    }

    @Test
    void receiveFromWeb() throws IOException, InterruptedException {
        Thread sendT = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("0.0.0.0", 9987);
                    Thread.sleep(1000); //
                    FrontBack.Product product = FrontBack.Product.newBuilder()
                            .setIid(1).setCount(2).setDescription("3").build();
                    FrontBack.FBMessage order = FrontBack.FBMessage.newBuilder()
                            .setPid(1).setProducts(product).setX(3).setY(4).build();
                    byte[] data = order.toByteArray();
                    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(socket.getOutputStream());
                    codedOutputStream.writeUInt32NoTag(data.length);
                    codedOutputStream.writeRawBytes(data);
                    codedOutputStream.flush();
                    System.out.println("data sent");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        sendT.start();

        WebIO webIO = WebIO.newInstance(9987);
        FrontBack.FBMessage.Builder fbBuilder = FrontBack.FBMessage.newBuilder();


        while (true) {
            System.out.println("wait to receive");
            webIO.receiveFromWeb(fbBuilder);
            System.out.println(fbBuilder.build());
        }
    }

}