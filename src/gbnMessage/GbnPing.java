package gbnMessage;

public class GbnPing implements GbnMessage {
    private final int seq;
    private final long time;
    private final int PACKET_NUMBER;

    public GbnPing(int seq, long time, int packetNumber) {
        this.seq = seq;
        this.time = time;
        this.PACKET_NUMBER = packetNumber;
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
        return PACKET_NUMBER;
    }

    @Override
    public String toString() {
        return "Ping[seq=" + seq + ", ts=" + time + "]";
    }
}