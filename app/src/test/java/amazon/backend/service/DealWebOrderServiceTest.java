package amazon.backend.service;

import amazon.backend.IO.WebIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.SingletonSessionFactory;
import amazon.backend.protobuf.FrontBack;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealWebOrderServiceTest {

    @Test
    void test_run() throws IOException {
        // mock an order
        FrontBack.FBMessage.Builder builder = FrontBack.FBMessage.newBuilder();
        FrontBack.Product product = FrontBack.Product.newBuilder()
                        .setIid(32).setCount(2).setDescription("Magic Johnson").build();
        builder.setPid(23).setProducts(product).setX(11).setY(28);
        FrontBack.FBMessage order = builder.build();

        DealWebOrderService dealWebOrderService =
                new DealWebOrderService(SingletonSessionFactory.getSessionFactory(), order);

        // Cannot find a good way to test singleton, this is partly testing
        assertThrows(IllegalStateException.class, () -> dealWebOrderService.run());
    }
}