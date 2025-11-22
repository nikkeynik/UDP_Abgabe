import java.net.*;
import java.io.*;

import message.*;

public class PingServer {

    public static void main(String[] args) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        System.out.println("[SERVER] Server läuft auf Port 9876");

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            try {
                Message ping = SimpleCodec.decode(receivePacket.getData());
                
                Message pong = new Pong(ping.getSeq(), System.nanoTime());
                
                InetAddress ipAdress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                 sendData = SimpleCodec.encode(pong);
                 
                 DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
                 serverSocket.send(sendPacket);

                 System.out.println("Ping empfangen, Pong gesendet. Checksumme OK");
            } catch (IllegalArgumentException e) {

                System.out.println("Fehler beim Prüfen der Cheksumme: " + e.getMessage());
                
            }
            
        }
    }
}
