package message;

public class Pong implements Message {
    private final int seq;
    private final LOcalTime time;

    public Pong(int seq, LocalTime time) {
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
    public LocalTime time() {
        return time;
    }

    @Override
    public String toString() {
        return "Pong[seq=" + seq + ", ts=" + time + "]";
    }
}
    