package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class WarehouseDao {
  Logger logger = LogManager.getLogger();

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

  public void clear() {
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction();

    String sql = "delete from warehouse";
    session.createNativeQuery(sql).executeUpdate();
    transaction.commit();
    logger.info("Clear warehouse table");

    session.close();
  }
}
