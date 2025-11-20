package message;

public class Pong implements Message {
    private final int seq;
    private final long time;

    public Pong(int seq, long time) {
        this.seq = seq;
        this.time = time;
    }

    @Override
    public MsgType getType() {
        return MsgType.PONG;
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
    public String toString() {
        return "Pong[seq=" + seq + ", ts=" + time + "]";
    }
}
    