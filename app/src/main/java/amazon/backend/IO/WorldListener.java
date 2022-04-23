package amazon.backend.IO;

import amazon.backend.manager.AckManager;
import amazon.backend.manager.LogisticsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.WorldAmazon;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Class to listen on WorldIO output stream.
 * Receive, identify, and dispatch message to its solver
 */
public class WorldListener implements Runnable{
    private static WorldListener INSTANCE;
    Logger logger = LogManager.getLogger();

    WorldIO worldIO;
    AckManager ackManager;
    LogisticsManager logisticsManager;

    public WorldListener(WorldIO worldIO, AckManager ackManager, LogisticsManager logisticsManager) {
        this.worldIO = worldIO;
        this.ackManager = ackManager;
        this.logisticsManager = logisticsManager;
        logger.info("New WorldListener instance constructed");
    }

    public static WorldListener getInstance() {
        return INSTANCE;
    }

    public static synchronized WorldListener newInstance(WorldIO worldIO, AckManager ackManager, LogisticsManager logisticsManager) {
        INSTANCE = new WorldListener(worldIO, ackManager, logisticsManager);
        return INSTANCE;
    }

    public void receive() throws IOException, SocketTimeoutException {
        WorldAmazon.AResponses.Builder responseBuilder = WorldAmazon.AResponses.newBuilder();
        worldIO.receiveFromWorld(responseBuilder);
        WorldAmazon.AResponses responses = responseBuilder.build();
        logger.info("Response: " + responses);
        dispatchPurchased(responses.getArrivedList());
        dispathAcks(responses.getAcksList());
        // TODO: dispatch others
    }

    private void dispatchPurchased(List<WorldAmazon.APurchaseMore> aPurchaseMoreList) {
        for (WorldAmazon.APurchaseMore aPurchaseMore: aPurchaseMoreList) {
            logger.info("Receive: \n" + aPurchaseMore);
            logisticsManager.purchaseArrived(aPurchaseMore);
        }
    }

    private void dispathAcks(List<Long> acks) {
        for (Long ack: acks) {
            logger.info("Receive ack: "+ack);
            ackManager.doAck(ack);
        }
    }

    @Override
    public void run() {
      logger.info("WorldListener starts running");
        while (true) {
            try {
                receive();
            } catch (SocketTimeoutException e) {
                // send buffer if read nothing
                if (!worldIO.isBufferEmpty()) {
                    try {
                        worldIO.sendBufferToWorld();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
