package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
import amazon.backend.IO.WebIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import protobuf.FrontBack;

import java.io.IOException;
import java.util.List;


public class DealWebOrderService implements Runnable {

    private FrontBack.FBMessage order;

    public DealWebOrderService(FrontBack.FBMessage order) {
        this.order = order;
    }

    /**
     * Deal with an order accepted from web service
     * add order(package) and product info to database
     * send response to web
     * request purchase from world
     */
    @Override
    public void run() {
      System.out.println("WebOrder: " + order.toString());
      
        PackageDao packageDao = new PackageDao();
        ProductDao productDao = new ProductDao();

        Package pkg = new Package(1, order.getPid(), order.getX(), order.getY());
        long packageId = packageDao.addOne(pkg);
        FrontBack.Product frontProdcut = order.getProducts();
        if (frontProdcut == null) {
            System.out.println("No product from web service");
        }
        Product product = new Product(frontProdcut.getIid(), frontProdcut.getDescription(), frontProdcut.getCount(), packageId);
        productDao.addOne(product);

        // Send response to web
        WebIO webIO = WebIO.getInstance();
        try {
            webIO.sendStatus(pkg.getOrderId(), "Packing");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // send purchase request to world
        WorldIO worldIO = WorldIO.getInstance();
        try {
            long seqNum = worldIO.sendAPurchaseMore(1, List.of(product));
            productDao.setBuySeq(product.getId(), packageId, seqNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
