package amazon.backend;

import amazon.backend.model.Package;
import amazon.backend.model.Product;
import amazon.backend.model.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SingletonSessionFactory {

    private static SessionFactory factory;

    public static synchronized SessionFactory getSessionFactory() {
        if (factory == null) {
            factory = new Configuration()
                    .configure()
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Package.class)
                    .addAnnotatedClass(Warehouse.class)
                    .buildSessionFactory();
            Logger logger = LogManager.getLogger();
            logger.info("Initialize session factory");
        }
        return factory;
    }
}
