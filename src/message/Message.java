package message;

public interface Message {
    MsgType getType();
    int getSeq();
    long getTime();
}