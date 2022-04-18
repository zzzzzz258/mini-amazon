package amazon.backend.DAO;

import amazon.backend.model.Product;
import amazon.backend.model.ProductPK;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ProductDao {

    private SessionFactory factory;

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

    /**
     * Select a product by its priamry key (id, packageId)
     * @param id
     * @param packageId
     * @return
     */
    public Product getOne(long id, long packageId) {
        ProductPK productPK = new ProductPK(id, packageId);
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Product product = session.get(Product.class, productPK);

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

        session.remove(new Product(id, "", 0, packageId));

        transaction.commit();
        session.close();
    }

    /**
     *
     * @param seqNum
     */
    public void setBuySeq(long id, long packageId, long seqNum) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Product product = session.get(Product.class, new ProductPK(id, packageId));
        product.setBuySeq(seqNum);
        session.merge(product);

        transaction.commit();
        session.close();
    }
}
