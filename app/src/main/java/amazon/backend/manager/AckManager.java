package amazon.backend.manager;

import org.hibernate.SessionFactory;

import java.util.Queue;

/**
 * Class to manage the ack message from world.
 * i.e. Offer new ack seq number into queue, then get it from queue and update in database
 */
public class AckManager implements Runnable {

    private SessionFactory sessionFactory;
    private Queue<Long> waitingQueue;
    private final Object lock = new Object();

    public AckManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Offer a new seqnum to waiting queue. This should be thread-safe
     * @param seq
     */
    public void offerAck(long seq) {
        synchronized (lock) {
            waitingQueue.offer(seq);
        }
    }

    /**
     * Get ack sequence number from waiting queue
     * @return -1 is waiting queue is empty
     */
    public long getAck() {
        long seq = -1;
        synchronized (lock) {
            if (!waitingQueue.isEmpty()) {
                seq = waitingQueue.poll();
            }
        }
        return seq;
    }

    /**
     * This Class continuously check the waitingQueue to update the acked message
     */
    @Override
    public void run() {
        while (true) {
            // TODO call DAO to update database
        }
    }
}
