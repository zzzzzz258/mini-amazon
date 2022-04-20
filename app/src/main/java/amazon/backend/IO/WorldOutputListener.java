package amazon.backend.IO;

import amazon.backend.manager.AckManager;
import protobuf.WorldAmazon;

import java.io.IOException;
import java.util.List;

/**
 * Class to listen on WorldIO output stream.
 * Receive, identify, and dispatch message to its solver
 */
public class WorldOutputListener implements Runnable{
    private static WorldOutputListener INSTANCE;

    WorldIO worldIO;
    AckManager ackManager;

    public WorldOutputListener(WorldIO worldIO, AckManager ackManager) {
        this.worldIO = worldIO;
        this.ackManager = ackManager;
    }

    public static WorldOutputListener getInstance() {
        return INSTANCE;
    }

    public static synchronized WorldOutputListener newInstance(WorldIO worldIO, AckManager ackManager) {
        INSTANCE = new WorldOutputListener(worldIO, ackManager);
        return INSTANCE;
    }

    public void receive() throws IOException {
        WorldAmazon.AResponses.Builder responseBuilder = WorldAmazon.AResponses.newBuilder();
        worldIO.receiveFromWorld(responseBuilder);
        WorldAmazon.AResponses responses = responseBuilder.build();
        System.out.println(responses);
        dispatchPurchased(responses.getArrivedList());
        dispathAcks(responses.getAcksList());
        // TODO: dispatch others
    }

    private void dispatchPurchased(List<WorldAmazon.APurchaseMore> aPurchaseMoreList) {
        for (WorldAmazon.APurchaseMore aPurchaseMore: aPurchaseMoreList) {
            // TODO: do real dispatch

        }
    }

    private void dispathAcks(List<Long> acks) {
        for (Long ack: acks) {
            ackManager.doAck(ack);
        }
    }

    @Override
    public void run() {
      System.out.println("World listener running");
        while (true) {
            try {
                receive();
            } catch (IOException e) {
                              e.printStackTrace();
            }
        }
    }
}
