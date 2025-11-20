import java.net.*;

import message.*;

import java.io.*;

public class PingClient {

    public static void main(String[] args) throws Exception {
        SimpleCodec codec = new SimpleCodec();

        // UDP-Socket anlegen
        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 9876;

        System.out.println("[CLIENT] Verbinde zu " + serverAddress + ":" + serverPort);

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        Message ping = new Ping(0, System.nanoTime());
        sendData = codec.encode(ping);

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

        clientSocket.send(sendPacket);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        clientSocket.receive(receivePacket);
        receiveData = receivePacket.getData();
        Message pong = codec.decode(receiveData);

        long zeitDist = System.nanoTime() - pong.getTime();






        clientSocket.close();
    }
}
