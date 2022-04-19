package amazon.backend.IO;

import amazon.backend.protobuf.FrontBack;

import java.io.IOException;
import java.util.List;

public class WebOutputListener {
    private WebIO webIO;

    public WebOutputListener(WebIO webIO) {
        this.webIO = webIO;
    }

    public void receive() throws IOException {
        FrontBack.FBMessage.Builder builder = FrontBack.FBMessage.newBuilder();
        webIO.receiveFromWeb(builder);
        FrontBack.FBMessage fbMessage = builder.build();
        System.out.println("From Web:\n" + fbMessage);
        dispatchOrder(fbMessage);
    }

    private void dispatchOrder(FrontBack.FBMessage order) {
        // TODO
    }
}
