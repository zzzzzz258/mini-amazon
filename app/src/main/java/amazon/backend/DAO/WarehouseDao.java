package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.Warehouse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class WarehouseDao {
  private SessionFactory factory;

  public WarehouseDao() {
    factory = SingletonSessionFactory.getSessionFactory();
  }

  public int addOne(Warehouse warehouse) {
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction();

    session.persist(warehouse);
    session.flush();

    transaction.commit();
    session.close();

    return warehouse.getId();
  }
}
