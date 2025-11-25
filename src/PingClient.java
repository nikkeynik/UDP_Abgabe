import java.net.*;
import message.*;

public class PingClient {

    public static void main(String[] args) throws Exception {
        long rtt = 5000;

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 9876;

        System.out.println("[CLIENT] Verbinde zu " + serverAddress + ":" + serverPort);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

       
        //korrekt
        Message ping = new Ping(0, System.nanoTime());
        byte[] sendData = SimpleCodec.encode(ping);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

        System.out.println("\n[CLIENT] Sende korrektes Message Ping");

        sendenUndEmpfangen(clientSocket, sendPacket, receivePacket, rtt, 0);// Zustand: Warten auf Antwort seq=0
        
        
        //fehlerhaft
        Message pingBad = new Ping(1, System.nanoTime());
        byte[] badData = SimpleCodec.encode(pingBad);

        badData[badData.length - 1] += 1;// beschädigen für falsche checksum
        System.out.println("\n[CLIENT] Sende fehlerhaftes Message Ping");

        try {
            SimpleCodec.decode(badData); // falls OK — sollte nicht sein
            DatagramPacket sendPacketBad =
                    new DatagramPacket(badData, badData.length, serverAddress, serverPort);
            sendenUndEmpfangen(clientSocket, sendPacketBad, receivePacket, rtt, 1);

        } catch (IllegalArgumentException e) {
            
            System.out.println("[CLIENT] Checksumme NICHT OK");// messadge ist beschädigt
            System.out.println(e.getMessage());

            DatagramPacket sendPacketBad =
                    new DatagramPacket(badData, badData.length, serverAddress, serverPort);

            // Zustand: Warten auf Antwort seq=1
            sendenUndEmpfangen(clientSocket, sendPacketBad, receivePacket, rtt, 1);
        }

        clientSocket.close();
        System.out.println("\nSocket geschlossen. Programmende");
    }

    private static void sendenUndEmpfangen(
            DatagramSocket socket,
            DatagramPacket sendPacket,
            DatagramPacket receivePacket,
            long timeoutMs,
            int seq
    ) {

        try {
            // sendPING()
            socket.send(sendPacket);
            socket.setSoTimeout((int) timeoutMs);

            System.out.println("Warte auf Antwort (Timeout: " + timeoutMs + " ms)...");

            // Warten auf Antwort
            socket.receive(receivePacket);
            byte[] data = receivePacket.getData();

            try {
                Message msg = SimpleCodec.decode(data);
                System.out.println("Checksumme überprüft: OK");

                long rttNano = System.nanoTime() - msg.getTime();
                System.out.println("Ping-Pong-Dauer: " + rttNano + " Nanosekunden");

            } catch (IllegalArgumentException e) {
                // besch'digt - Zustand bleibt gleich
                System.out.println(e.getMessage());
            }

        } catch (SocketTimeoutException e) {
            // timeout - resendPing(seq)
            System.out.println("Timeout! Keine Antwort erhalten.");
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
}
