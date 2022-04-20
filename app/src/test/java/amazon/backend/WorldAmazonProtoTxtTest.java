package amazon.backend;

import protobuf.WorldAmazon;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldAmazonTest {
    @Test
    public void test_simple_use() throws IOException {
        long worldid = 1;
        WorldAmazon.AInitWarehouse wh0 =
                WorldAmazon.AInitWarehouse.newBuilder()
                        .setId(1)
                        .setX(5)
                        .setY(5)
                        .build();
        WorldAmazon.AConnect connect =
                WorldAmazon.AConnect.newBuilder()
                        .setWorldid(worldid)
                        .setIsAmazon(true)
                        .addInitwh(wh0)
                        .build();
        assertEquals(worldid, connect.getWorldid());
        assertTrue(connect.getIsAmazon());
        assertEquals(wh0, connect.getInitwhList().get(0));

        FileOutputStream fos = new FileOutputStream("testfile1");
        connect.writeTo(fos);

        WorldAmazon.AConnect deserialized =
                WorldAmazon.AConnect.newBuilder()
                        .mergeFrom(new FileInputStream("testfile1"))
                        .build();

        assertEquals(true, deserialized.getIsAmazon());
        assertEquals(wh0, deserialized.getInitwh(0));
        assertEquals(worldid, deserialized.getWorldid());

    }
}