package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.Package;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PackageDaoTest {

    SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();

    @Test
    void addOne_getOne_deleteOne() {
        PackageDao packageDao = new PackageDao(sessionFactory);

        Package p = new Package(23, 22, 10, 10);
        long id = packageDao.addOne(p);
        Package pig = packageDao.getOne(id);
        assertEquals(23, pig.getWarehouseId());
        packageDao.deleteOne(id);
        assertNull(packageDao.getOne(id));
    }
}