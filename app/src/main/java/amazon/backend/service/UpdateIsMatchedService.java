package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.IO.UpsIO;
import amazon.backend.IO.WebIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protobuf.AmazonUps;

public class UpdateIsMatchedService implements Runnable {
  Logger logger = LogManager.getLogger();

  private AmazonUps.UAIsAssociated isAssociated;

  public UpdateIsMatchedService(AmazonUps.UAIsAssociated isAssociated) {
    this.isAssociated = isAssociated;
  }

  @Override
  public void run() {
    WebIO webIO = WebIO.getInstance();
    long packageId = isAssociated.getPackageid();
    PackageDao packageDao = new PackageDao();

    webIO.sendIsMatched(packageDao.getOrderId(packageId), isAssociated.getCheckResult());
  }
}
