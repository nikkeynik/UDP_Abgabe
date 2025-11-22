package gbnMessage;

public class GbnPing implements GbnMessage {
    private final int seq;
    private final long time;

    public GbnPing(int seq, long time) {
        this.seq = seq;
        this.time = time;
    }

    @Override
    public GbnMsgType getType() {
        return GbnMsgType.PING;
    }

    @Override
    public int getSeq() {
        return seq;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public int getPacketNr() {
        return 0;
    }

    @Override
    public String toString() {
        return "Ping[seq=" + seq + ", ts=" + time + "]";
    }
}