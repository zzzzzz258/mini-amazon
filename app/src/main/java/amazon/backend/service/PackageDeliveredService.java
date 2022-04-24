package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.IO.UpsIO;
import amazon.backend.IO.WebIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.AmazonUps;

import java.util.HashSet;
import java.util.Set;

public class PackageDeliveredService implements Runnable{
  Logger logger = LogManager.getLogger();

  private AmazonUps.UAPackageDelivered packageDelivered;

  Set<Long> upsAcks = new HashSet<>();

  public PackageDeliveredService(AmazonUps.UAPackageDelivered packageDelivered) {
    this.packageDelivered = packageDelivered;
  }

  @Override
  public void run() {
    if (!upsAcks.contains(packageDelivered.getSeqnum())) {
      upsAcks.add(packageDelivered.getSeqnum());

      long packageId = packageDelivered.getPackageid();
      // send shit back
      UpsIO upsIO = UpsIO.getInstance();
      upsIO.sendAck(packageDelivered.getSeqnum());

      // send to zz
      WebIO webIO = WebIO.getInstance();
      PackageDao packageDao = new PackageDao();
      webIO.sendStatus(packageDao.getOrderId(packageId), "Delivered");
    }
  }
}
