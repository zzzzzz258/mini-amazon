package amazon.backend.IO;

import amazon.backend.manager.LogisticsManager;
import amazon.backend.protobuf.FrontBack;

import java.io.IOException;
import java.util.List;

public class WebOutputListener implements Runnable {

    private static WebOutputListener INSTANCE;

    private WebIO webIO;
    private LogisticsManager logisticsManager;

    private WebOutputListener(WebIO webIO, LogisticsManager logisticsManager) {
        this.webIO = webIO;
        this.logisticsManager = logisticsManager;
    }

    public static WebOutputListener getInstance() {
        return INSTANCE;
    }

    public static synchronized WebOutputListener newInstance(WebIO webIO, LogisticsManager logisticsManager) {
        INSTANCE = new WebOutputListener(webIO, logisticsManager);
        return INSTANCE;
    }

    public void receive() throws IOException {
      FrontBack.FBMessage.Builder builder = FrontBack.FBMessage.newBuilder();
      if (webIO.receiveFromWeb(builder) == true) {
        FrontBack.FBMessage fbMessage = builder.build();
        System.out.println("From Web:\n" + fbMessage);
        dispatchOrder(fbMessage);
      }
    }

    private void dispatchOrder(FrontBack.FBMessage order) {
      System.out.println("Dispatch to manager: "+order.toString());
      logisticsManager.confirmOrder(order);
    }

    @Override
    public void run() {
      while (true) {
            try {
                receive();
            } catch (IOException e) {
              e.printStackTrace();
            }
        }
    }
}
