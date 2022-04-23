package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.IO.WorldIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.WorldAmazon;

public class PackagePackedService implements Runnable {

  Logger logger = LogManager.getLogger();

  private WorldAmazon.APacked aPacked;

  public PackagePackedService(WorldAmazon.APacked aPacked) {
    this.aPacked = aPacked;
  }

  @Override
  public void run() {
    // send ACK back
    logger.info("Processing response APacked: " + aPacked.getSeqnum());
    WorldIO worldIO = WorldIO.getInstance();
    PackageDao packageDao = new PackageDao();

    worldIO.sendAck(aPacked.getSeqnum());

    // update databse, record packed
    packageDao.setPacked(aPacked.getShipid());

    // TODO send to ups for pick our shit

  }
}
