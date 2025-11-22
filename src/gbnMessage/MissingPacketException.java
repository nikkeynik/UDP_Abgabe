package gbnMessage;

public class MissingPacketException extends Exception{
    public MissingPacketException(){
        super();
    }

    public MissingPacketException(String msg){
        super(msg);
    }
}