package amazon.backend;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

public class MockWebTest {
//  public static final String IP = "vcm-25372.vm.duke.edu";
public static final String IP = "0.0.0.0";
  private final int myWebPort = 2222;

  @Test
  public void connect2Amazon() throws IOException {
    Socket socket = new Socket(IP, myWebPort);
    System.out.println("Connected");
    while(true);
  }
}
