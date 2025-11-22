package gbnMessage;

public class GbnPong implements GbnMessage {
    private final int seq;
    private final long time;
    private final int PACKET_NUMBER;

    public GbnPong(int seq, long time, int packetNumber) {
        this.seq = seq;
        this.time = time;
        this.PACKET_NUMBER = packetNumber;
    }

    @Override
    public GbnMsgType getType() {
        return GbnMsgType.PONG;
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
        return "Pong[seq=" + seq + ", ts=" + time + "]";
    }
}
    