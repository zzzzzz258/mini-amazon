package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.Package;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PackageDaoTest {

    @Test
    void addOne() {
        SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
        PackageDao packageDao = new PackageDao(sessionFactory);

        Package p = new Package(23, 22, 10, 10);
        long id = packageDao.addOne(p);
        Package pig = packageDao.getOne(id);

        System.out.println(id);
        assertEquals(23, pig.getWarehouseId());
    }
}