package amazon.backend.service;

import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.IO.WebIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.WorldMessage;
import protobuf.FrontBack;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class DealWebOrderServiceTest {

    @Test
    void test_run() throws IOException {
        // prepare prduct and package and world_message
        long packageId = 987804;
        long productId = 778642;
        long seqNum = 667842;
        //PackageDao packageDao = new PackageDao();
        //ProductDao productDao = new ProductDao();
        //packageDao.addOne(new Package(packageId));
        //productDao.addOne(new Product(productId, "", 0, packageId));
        WorldMessageDao worldMessageDao = new WorldMessageDao();
        if (worldMessageDao.getOne(seqNum) == null)
            worldMessageDao.addOne(new WorldMessage(seqNum));


        // mock an order
        FrontBack.FBMessage.Builder builder = FrontBack.FBMessage.newBuilder();
        FrontBack.Product product = FrontBack.Product.newBuilder()
                        .setIid(productId).setCount(2).setDescription("Magic Johnson").build();
        builder.setPid(23).setProducts(product).setX(11).setY(28);
        FrontBack.FBMessage order = builder.build();

        WebIO mockWebIO = mock(WebIO.class);
        WebIO.newInstance(mockWebIO);
        WorldIO mockWorldIO = mock(WorldIO.class);
        WorldIO.newInstance(mockWorldIO);
        doReturn(seqNum).when(mockWorldIO).sendAPurchaseMore(anyInt(), any());

        DealWebOrderService dealWebOrderService =
                new DealWebOrderService(order);

        dealWebOrderService.run();

        verify(mockWebIO).sendStatus(anyInt(), any());
        verify(mockWorldIO).sendAPurchaseMore(anyInt(),any());
    }
}