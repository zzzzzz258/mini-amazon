package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.WorldMessageDao;
import amazon.backend.IO.UpsIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.WorldMessage;
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
    // send useles ack back if needed
    UpsIO upsIO = UpsIO.getInstance();
    upsIO.sendAck(uaReadyForPickup.getSeqnum());

    // tell world to load to the truck, update truck id on database
    PackageDao packageDao = new PackageDao();
    WorldMessageDao worldMessageDao = new WorldMessageDao();
    WorldIO worldIO = WorldIO.getInstance();
    uaReadyForPickup.getPackageidList().stream().forEach(id -> {
      packageDao.setTruckId(id, uaReadyForPickup.getTruckid());
      long seqNum = worldIO.sendAPutOnTruck(uaReadyForPickup.getWhnum(), uaReadyForPickup.getTruckid(), id);
      worldMessageDao.addOne(new WorldMessage(seqNum));
      packageDao.setLoadSeq(id, seqNum);
    });

  }
}
