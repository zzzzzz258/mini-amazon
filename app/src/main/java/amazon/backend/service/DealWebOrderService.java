package amazon.backend.service;

import amazon.backend.DAO.PackageDao;
import amazon.backend.DAO.ProductDao;
import amazon.backend.IO.WebIO;
import amazon.backend.IO.WorldIO;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import amazon.backend.protobuf.FrontBack;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.List;


public class DealWebOrderService implements Runnable {

    private SessionFactory sessionFactory;
    private FrontBack.FBMessage order;

    public DealWebOrderService(SessionFactory sessionFactory, FrontBack.FBMessage order) {
        this.sessionFactory = sessionFactory;
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
        PackageDao packageDao = new PackageDao(sessionFactory);
        ProductDao productDao = new ProductDao(sessionFactory);

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
