package amazon.backend.model;

import jakarta.persistence.*;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.cfg.annotations.reflection.internal.XMLContext;

@Entity
@Table(name = "world_message")
public class WorldMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sequence_num")
    private long sequenceNum;
    private boolean acked;
    @Column(name = "sent_time")
    private long sentTime;
    private int simspeed;
    private boolean disconnect;

    private static final int defaultSimspeed = 100;

    public WorldMessage() {
    }

    public WorldMessage(long sequenceNum) {
        this(sequenceNum, System.currentTimeMillis());
    }

    public WorldMessage(long sequenceNum, long sentTime) {
        this(sequenceNum, false, sentTime, defaultSimspeed, false);
    }

    public WorldMessage(long sequenceNum, boolean acked, long sentTime, int simspeed, boolean disconnect) {
        this.sequenceNum = sequenceNum;
        this.acked = acked;
        this.sentTime = sentTime;
        this.simspeed = simspeed;
        this.disconnect = disconnect;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public boolean isAcked() {
        return acked;
    }

    public void setAcked(boolean acked) {
        this.acked = acked;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public int getSimspeed() {
        return simspeed;
    }

    public void setSimspeed(int simspeed) {
        this.simspeed = simspeed;
    }

    public boolean isDisconnect() {
        return disconnect;
    }

    public void setDisconnect(boolean disconnect) {
        this.disconnect = disconnect;
    }
}
