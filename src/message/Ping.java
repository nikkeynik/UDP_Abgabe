package message;


public class Ping implements Message {
    private final int seq;
    private final long time;
    private final int checksum;

    public Ping(int seq, long time) {
        this.seq = seq;
        this.time = time;
        this.checksum = berechneChecksum();
    }

    public Ping(int seq, long time, int checksum) {
        this.seq = seq;
        this.time = time;
        this.checksum = checksum;
    }

    private int berechneChecksum() {
        String data = "PING" + seq + time;
        int sum = 0;
        for (char c : data.toCharArray()) sum += c;
        return sum % 256;
    }

    @Override
    public MsgType getType() {
        return MsgType.PING;
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
        return "Ping [ seq=" + seq + ", ts=" + time + ", checksum=" + checksum + " ]";
    }
}