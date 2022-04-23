package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ProductDao {
    Logger logger = LogManager.getLogger();

    private SessionFactory factory;

    public ProductDao() {
        factory = SingletonSessionFactory.getSessionFactory();
    }

    public ProductDao(SessionFactory factory) {
        this.factory = factory;
    }

    /**
     * Method to insert a new product
     * @param product
     */
    public void addOne(Product product) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        session.persist(product);
        transaction.commit();

        session.close();
    }

    public Product getOne(long packageId) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Product product = session.get(Product.class, packageId);

        transaction.commit();
        session.close();
        return product;
    }

    /**
     * Delete one product based on its primary key
     * @param id
     * @param packageId
     */
    public void deleteOne(long id, long packageId) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        session.remove(new Product(id));

        transaction.commit();
        session.close();
    }

    public void setBuySeq(long packageId, long seqNum) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Product product = session.get(Product.class, packageId);
        product.setBuySeq(seqNum);
        session.merge(product);

        transaction.commit();
        session.close();
    }


    public long productBought(long productId, int count) {
        while (true) {
            Session session = factory.openSession();
            Transaction transaction = session.beginTransaction();

            String sql = "select * from product where product_id = ? and count = ? " +
                    "and buy_seq is not null and is_bought is not true order by buy_seq asc limit 1";
            List<Product> results = session.createNativeQuery(sql, Product.class).setParameter(1, productId)
                    .setParameter(2, count)
                    .list();
            try {
                Product product = results.get(0);
                product.setBought(true);
                session.merge(product);
                transaction.commit();
                logger.info("Product arrived: " + productId + " * " + count);
                return product.getPackageId();
            } catch (IndexOutOfBoundsException e) {
                logger.error("Arrived product not in record: " + productId + " * " + count);
                return -1;
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            finally {
                session.close();
            }
        }
    }

    public boolean checkPackageReady(long packageId) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        String sql = "select * from product where package_id = ? and is_bought is not true";
        List<Product> results = session.createNativeQuery(sql, Product.class).setParameter(1, packageId).list();

        session.close();

        return results.isEmpty();
    }

    public List<Product> getPackageProducts(long packageId) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        String sql = "select * from product where package_id = ?";
        List<Product> results = session.createNativeQuery(sql, Product.class).setParameter(1, packageId).list();

        session.close();

        return results;
    }
}
