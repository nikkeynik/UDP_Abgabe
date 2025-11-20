import java.net.*;

import message.*;

import java.io.*;

public class PingClient {

    public static void main(String[] args) throws Exception {

        // UDP-Socket anlegen
        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = 9876;

        System.out.println("[CLIENT] Verbinde zu " + serverAddress + ":" + serverPort);

        
        clientSocket.close();
    }
}
