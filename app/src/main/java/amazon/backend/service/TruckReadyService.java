package amazon.backend.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.AmazonUps;

public class TruckReadyService implements Runnable{
  Logger logger = LogManager.getLogger();

  private AmazonUps.UAReadyForPickup uaReadyForPickup;

  public TruckReadyService(AmazonUps.UAReadyForPickup uaReadyForPickup) {
    this.uaReadyForPickup = uaReadyForPickup;
  }


  @Override
  public void run() {
    // send useles ack back

    // tell world to load to the truck, update truck id on database
  }
}
