package amazon.backend.IO;

import amazon.backend.manager.LogisticsManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class WebOutputListenerTest {

    @Mock
    WebIO mockWebIO;
    @Mock
    LogisticsManager logisticsManager;

    @Test
    void getInstance() {
        assertNull(WebOutputListener.getInstance());
    }

    @Test
    void run() throws IOException {
        WebOutputListener webOutputListener = WebOutputListener.newInstance(mockWebIO, logisticsManager);
        assertNotNull(webOutputListener);

        webOutputListener.receive();
        verify(mockWebIO).receiveFromWeb(any());
        verify(logisticsManager).confirmOrder(any());

    }
}