package amazon.backend.model;

import org.hibernate.cfg.annotations.reflection.internal.XMLContext;

public class WorldMessage {
    private long sequenceNum;
    private boolean acked;
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
