import java.net.*;

import gbnMessage.*;
import message.*;

import java.io.*;

import static gbnMessage.GbnMsgType.PONG;

public class GoBackNServer {

    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        int actNr = -1;

        System.out.println("[SERVER] Server läuft auf Port 9876");

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            GbnMessage ping = GbnSimpleCodec.decode(receivePacket.getData());
            try{
                if(actNr+1 != ping.getPacketNr()){
                    throw new MissingPacketException();
                }
                else{
                    actNr = ping.getPacketNr();
                }

                GbnMessage pong = new GbnSimpleMessage(PONG, ping.getSeq(), System.nanoTime(), ping.getPacketNr());

                InetAddress ipAdress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                byte[] sendData = GbnSimpleCodec.encode(pong);

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
                serverSocket.send(sendPacket);
            } catch (MissingPacketException e){
                System.out.println("Packet " + (actNr+1) + " fehlt. Packet " + ping.getPacketNr() + "wird verworfen.");
                GbnMessage pong = new GbnSimpleMessage(PONG, ping.getSeq(), System.nanoTime(), actNr);
                InetAddress ipAdress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                byte[] sendData = GbnSimpleCodec.encode(pong);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
                serverSocket.send(sendPacket);
            }
        }
    }
}
