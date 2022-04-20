package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.Package;
import amazon.backend.model.Product;
import amazon.backend.model.WorldMessage;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoTest {

    @Test
    void test_a_lot() {
        SessionFactory factory = SingletonSessionFactory.getSessionFactory();

        ProductDao productDao = new ProductDao(factory);
        PackageDao packageDao = new PackageDao(factory);
        WorldMessageDao worldMessageDao = new WorldMessageDao(factory);


        long productId = 1998071616;
        long seqNum = 2022041919;

        if (worldMessageDao.getOne(seqNum) == null) {
            worldMessageDao.addOne(new WorldMessage(seqNum));
        }
        long packageId = packageDao.addOne(new Package(89, 80, 10, 10));
        // test getOne and deleteOne
        if (productDao.getOne(productId, packageId)!=null) {
            productDao.deleteOne(productId, packageId);
        }
        assertNull(productDao.getOne(productId, packageId));

        // Test addOne
        productDao.addOne(new Product(productId, "holy shit", 3, packageId));
        Product product1 = productDao.getOne(productId, packageId);
        assertNotNull(product1);
        assertEquals("holy shit", product1.getDescription());
        assertNull(product1.getBuySeq());

        // Test setBuySeq
        productDao.setBuySeq(productId, packageId, seqNum);
        Product product2 = productDao.getOne(productId, packageId);
        assertEquals(seqNum, product2.getBuySeq());

    }
}