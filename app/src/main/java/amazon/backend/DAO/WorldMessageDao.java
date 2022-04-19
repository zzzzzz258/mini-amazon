package amazon.backend.DAO;

import amazon.backend.SingletonSessionFactory;
import amazon.backend.model.WorldMessage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class WorldMessageDao {

    private SessionFactory factory;

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
            session.persist(worldMessage);
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
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        WorldMessage worldMessage = session.get(WorldMessage.class, seqNum);
        worldMessage.setAcked(true);
        session.merge(worldMessage);

        transaction.commit();
        session.close();

        System.out.println("Ack: " + seqNum);
    }
}
