package amazon.backend.IO;

import amazon.backend.manager.AckManager;
import amazon.backend.protobuf.WorldAmazon;

import java.io.IOException;
import java.util.List;

/**
 * Class to listen on WorldIO output stream.
 * Receive, identify, and dispatch message to its solver
 */
public class WorldOutputListener {
    WorldIO worldIO;
    AckManager ackManager;

    public WorldOutputListener(WorldIO worldIO) {
        this.worldIO = worldIO;
    }

    public WorldOutputListener(WorldIO worldIO, AckManager ackManager) {
        this.worldIO = worldIO;
        this.ackManager = ackManager;
    }

    public void receive() throws IOException {
        WorldAmazon.AResponses.Builder responseBuilder = WorldAmazon.AResponses.newBuilder();
        worldIO.receiveFromWorld(responseBuilder);
        WorldAmazon.AResponses responses = responseBuilder.build();
        //System.out.println(responses);
        dispatchPurchased(responses.getArrivedList());
        dispathAcks(responses.getAcksList());
        // TODO: dispatch others
    }

    private void dispatchPurchased(List<WorldAmazon.APurchaseMore> aPurchaseMoreList) {
        for (WorldAmazon.APurchaseMore aPurchaseMore: aPurchaseMoreList) {
            // TODO: do real dispatch
            System.out.println(aPurchaseMore);
        }
    }

    private void dispathAcks(List<Long> acks) {
        for (Long ack: acks) {
            System.out.println(ack);
            //TODO: do real dispatch
        }
    }
}
