package amazon.backend.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.WorldAmazon;

public class PackageLoadedService implements Runnable{
  Logger logger = LogManager.getLogger();

  private WorldAmazon.ALoaded aLoaded;

  public PackageLoadedService(WorldAmazon.ALoaded aLoaded) {
    this.aLoaded = aLoaded;
  }

  @Override
  public void run() {
    // send ACK back

    // update database to loaded

    // send zz new status

    // check if all loaded, if so send ups to go
  }
}
