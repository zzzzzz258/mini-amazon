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

        int id = 90;

        packageDao.deleteOne(id);

        Package p = new Package(id, 23, 22);
        packageDao.addOne(p);
        Package pig = packageDao.getOne(id);

        assertEquals(23, pig.getWarehouseId());
    }
}