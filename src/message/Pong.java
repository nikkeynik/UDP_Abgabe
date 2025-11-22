package message;

public class Pong implements Message {
    private final int seq;
    private final long time;
    private final int checksum;

    public Pong(int seq, long time) {
        this.seq = seq;
        this.time = time;
        this.checksum = computeChecksum();
    }

    public Pong(int seq, long time, int checksum) {
        this.seq = seq;
        this.time = time;
        this.checksum = checksum;
    }

    private int computeChecksum() {
        String data = "PONG" + seq + time;
        int sum = 0;
        for (char c : data.toCharArray()) sum += c;
        return sum % 256;
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
    public int getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        return "Pong [ seq=" + seq + ", ts=" + time + ", checksum=" + checksum + " ]";
    }
}
    