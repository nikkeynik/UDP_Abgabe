package message;

public class Pong implements Message {
    private final int seq;
    private final long time;

    public Pong(int seq, long time) {
        this.seq = seq;
        this.time = time;
    }

    @Override
    public MsgType type() {
        return MsgType.PONG;
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
        return "Pong[seq=" + seq + ", ts=" + time + "]";
    }
}
    