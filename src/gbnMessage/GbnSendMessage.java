package gbnMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GbnSendMessage extends Thread{
    private final long TIMEOUT = 5000;

    public GbnSendMessage(DatagramSocket socket, DatagramPacket packet) throws IOException {
        socket.send(packet);
        socket.setSoTimeout((int) TIMEOUT);
        int packetNr = GbnSimpleCodec.decode(packet.getData()).getPacketNr();

        System.out.println("Warte auf UDP-Paket "+ packetNr+" (Timeout: " + TIMEOUT + " Millisekunden)...");
    }
}
