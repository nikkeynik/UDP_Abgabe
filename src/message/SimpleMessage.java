package message;

public class SimpleMessage implements Message {

    private final MsgType type;
    private final int seq;
    private final int checksum;

     public SimpleMessage(MsgType type, int seq) {
        this.type = type;
        this.seq = seq;
        this.checksum = calculateChecksum();
    }

     public SimpleMessage(MsgType type, int seq, int checksum) {
        this.type = type;
        this.seq = seq;
        this.checksum = checksum;
    }

    private int calculateChecksum() {
        String data = type.name() + seq;
        int sum = 0;
        for (char c : data.toCharArray()) sum += c;
        return sum % 256;
    }

    @Override
    public MsgType getType() { 
        return type; 
    }

    @Override
    public int getSeq() { 
        return seq; 
    }

    @Override
    public int getChecksum() { 
        return checksum; 
    }

    @Override
    public String toString() {
        return type + " [ seq=" + seq + ", checksum=" + checksum + " ]";
    }
}
