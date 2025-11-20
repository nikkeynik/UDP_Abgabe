import java.net.*;


import java.io.*;

public class PingServer {

    public static void main(String[] args) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];

        System.out.println("[SERVER] Server läuft auf Port 9876");

    }
}
