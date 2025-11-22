package gbnMessage;

public interface GbnMessage {
    GbnMsgType getType();
    int getSeq();
    long getTime();
    int getPacketNr();
}