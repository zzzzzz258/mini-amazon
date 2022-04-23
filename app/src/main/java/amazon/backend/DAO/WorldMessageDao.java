package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.WorldMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.postgresql.util.PSQLException;

public class WorldMessageDao {

    private SessionFactory factory;
    Logger logger = LogManager.getLogger(WorldMessageDao.class);

    public WorldMessageDao() {
        factory = SingletonSessionFactory.getSessionFactory();
    }

    public WorldMessageDao(SessionFactory factory) {
        this.factory = factory;
    }

    /**
     * Mwthod to add a WorldMessage to data base
     * @param worldMessage
     */
    public void addOne(WorldMessage worldMessage) {
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.merge(worldMessage);
            transaction.commit();
        }
        catch (HibernateException e) {
            if (transaction!=null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        finally {
            session.close();
        }
    }

    /**
     * Select one by its sequence number
     * @param seqNum
     * @return null if target not found
     */
    public WorldMessage getOne(long seqNum) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        WorldMessage result = session.get(WorldMessage.class, seqNum);

        transaction.commit();
        session.close();

        return result;
    }

    /**
     * Method to delete one worldMessage by its seqNUm
     * @param seqNum
     */
    public void deleteOne(long seqNum) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        session.remove(new WorldMessage(seqNum));

        transaction.commit();
        session.close();
    }

    /**
     * Update the given world message to acked
     * @param seqNum
     */
    public void ackOne(long seqNum) {
        while (true) {
            Session session = factory.openSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                WorldMessage worldMessage = session.get(WorldMessage.class, seqNum);
                worldMessage.setAcked(true);
                session.merge(worldMessage);

                transaction.commit();
                logger.info("Ack " + seqNum + " successful");
                session.close();
                return;
            } catch (NullPointerException e) {
                logger.error("Ack " + seqNum + " fails, record doesn't exists");
                transaction.rollback();
            } catch (Exception e) {
                logger.warn(e.getMessage());
            } finally {
                session.close();
            }
        }
    }
}
