package amazon.backend.IO;

import amazon.backend.manager.LogisticsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.FrontBack;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class WebListener implements Runnable {

    private static WebListener INSTANCE;
    Logger logger = LogManager.getLogger(WebListener.class);

    private WebIO webIO;
    private LogisticsManager logisticsManager;

    private WebListener(WebIO webIO, LogisticsManager logisticsManager) {
        this.webIO = webIO;
        this.logisticsManager = logisticsManager;
        logger.info("WebListener new Instance constructed");
    }

    public static WebListener getInstance() {
        return INSTANCE;
    }

    public static synchronized WebListener newInstance(WebIO webIO, LogisticsManager logisticsManager) {
        INSTANCE = new WebListener(webIO, logisticsManager);
        return INSTANCE;
    }

    public void receive() throws IOException {
      FrontBack.FBMessage.Builder builder = FrontBack.FBMessage.newBuilder();
      webIO.receiveFromWeb(builder);
      FrontBack.FBMessage fbMessage = builder.build();
      logger.info("Receive FBMessage from web:\n" + fbMessage);
      dispatchOrder(fbMessage);
    }

    private void dispatchOrder(FrontBack.FBMessage order) {
      logisticsManager.confirmOrder(order);
    }

    @Override
    public void run() {
      logger.info("WebListener starts running");
      while (true) {
        try {
          receive();
        }
        catch (SocketTimeoutException e) {
          webIO.popQueueToWeb();
          //logger.warn("Socket time out");
        }
        catch (IOException e) {
          logger.error(e.getStackTrace());
        }
        catch (Exception e) {
          logger.error(e.getStackTrace());
        }
      }
    }
}
