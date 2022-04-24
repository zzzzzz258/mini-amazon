package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.IO.UpsIO;
import amazon.backend.IO.WebIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.Package;
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
    WorldIO worldIO = WorldIO.getInstance();
    worldIO.sendAck(aLoaded.getSeqnum());

    // update database to loaded
    PackageDao packageDao = new PackageDao();
    packageDao.setLoaded(aLoaded.getShipid());
    Package pkg = packageDao.getOne(aLoaded.getShipid());

    // send zz new status
    WebIO webIO = WebIO.getInstance();
    webIO.sendStatus(pkg.getOrderId(), "Shipping");

    // check if all loaded, if so send ups to go
    if (packageDao.checkPackageLoaded(pkg.getTruckId())) {
      UpsIO upsIO = UpsIO.getInstance();
      upsIO.sendAUReadyForDelivery(pkg.getTruckId());
    }
  }
}
