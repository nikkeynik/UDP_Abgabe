package message;

public class Ping implements Message {
    private final int seq;
    private final long time;

    public Ping(int seq, long time) {
        this.seq = seq;
        this.time = time;
    }

    @Override
    public MsgType type() {
        return MsgType.PING;
    }

    @Override
    public int seq() {
        return seq;
    }

    @Override
    public long time() {
        return time;
    }

    @Override
    public String toString() {
        return "Ping[seq=" + seq + ", ts=" + time + "]";
    }
}