package message;

public interface Message {
    MsgType type();
    int seq();
    long time();
}