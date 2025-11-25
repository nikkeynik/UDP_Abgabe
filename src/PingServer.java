import java.net.*;
import java.io.*;

import message.*;

public class PingServer {

   public static void main(String[] args) throws Exception {
    System.out.println("[SERVER] Server läuft auf Port 9876");

    try (DatagramSocket serverSocket = new DatagramSocket(9876)) {
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            try {
                Message ping = SimpleCodec.decode(receivePacket.getData()); //Zustand: Empfange PING (seq = 0 oder seq = 1)/

                Message pong = new Pong(ping.getSeq(), System.nanoTime()); //erstellePong(seq, time, checksum)

                InetAddress ipAdress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                sendData = SimpleCodec.encode(pong); //erstellePong(seq, time, checksum)
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
                serverSocket.send(sendPacket);

                System.out.println("Ping empfangen, Pong gesendet. Checksumme OK");

            } catch (IllegalArgumentException e) {
                System.out.println("Fehler beim Prüfen der Checksumme: " + e.getMessage());
            }
        }
    }
}
}
