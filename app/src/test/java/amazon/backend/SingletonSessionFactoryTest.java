package amazon.backend;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingletonSessionFactoryTest {
    @Test
    public void test_getInstance() {
        SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
        assertNotNull(sessionFactory);
    }
}