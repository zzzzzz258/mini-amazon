package amazon.backend.DAO;

import amazon.backend.model.WorldMessage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class WorldMessageDao {

    private SessionFactory factory;
    //private final String update = "update world_message set acked = true where sequence_num =:seqNum";

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
    }
}
