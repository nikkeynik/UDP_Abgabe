import message.Message;
import message.Ping;
import message.SimpleCodec;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class GoBackNClient {
    public static void main(String[] args) throws Exception {
        final int N = 4;
        long rtt = 5000;
        boolean timeout = true;


        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 9876;

        System.out.println("[CLIENT] Verbinde zu " + serverAddress + ":" + serverPort);


        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        for(int i = 1; i < N+1; i++) {
            Message ping = new Ping(0, System.nanoTime());
            sendData = SimpleCodec.encode(ping);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while(timeout){
                timeout = false;
                clientSocket.send(sendPacket);

                clientSocket.setSoTimeout((int) rtt);
                System.out.println("Warte auf UDP-Paket (Timeout: " + rtt + " Millisekunden)...");

                try{

                    clientSocket.receive(receivePacket);
                    receiveData = receivePacket.getData();
                    Message pong = SimpleCodec.decode(receiveData);

                    long zeitDist = System.nanoTime() - pong.getTime();

                    System.out.println("Ping-Pong-Dauer: " + (zeitDist) + " Nanosekunden");

                } catch (SocketTimeoutException e){
                    System.out.println("Timeout: Innerhalb von " + rtt +" Millisekunden wurde nichts empfangen.");
                    timeout = true;
                }

            }


            clientSocket.close();
            System.out.println("Socket geschlossen. Programm Ende");
        }
        }

}
