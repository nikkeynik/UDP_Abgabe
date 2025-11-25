import java.net.*;

import gbnMessage.GbnMessage;
import gbnMessage.GbnPing;
import gbnMessage.GbnSimpleCodec;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GoBackNClient extends Thread {
    final int WINDOW = 5;
    final float alpha = 0.125;
    long estimatedRTT = 3_000_000_000L;
    long sampleRTT;

    int base = 0;
    int nextSeqNum = 0;
    Map<Integer, Long> timestamps = new ConcurrentHashMap<>();
    CopyOnWriteArrayList<Integer> received = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        DatagramSocket socket;
        try { socket = new DatagramSocket(); }
        catch (Exception e) { throw new RuntimeException(e); }

        InetAddress ipAdress;
        try { ipAdress = InetAddress.getByName("localhost"); }
        catch (Exception e) { throw new RuntimeException(e); }

        int port = 9876;

        // receive
        new Thread(() -> {
            while(true) {
                try {
                    byte[] receiveBytes = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
                    socket.receive(receivePacket);
                    GbnMessage receiveMessage = GbnSimpleCodec.decode(receiveBytes);

                    received.add(receiveMessage.getPacketNr());

                    System.out.println(   "|  ACK für Packet Nummer " + receiveMessage.getPacketNr()+ " empfangen."+
                                        "\n|  Dauer der Übertragung: "+ calculateDuration(receiveMessage.getPacketNr())+" Sekunden.\n\n");
                    sampleRTT = calculateDuration(receiveMessage.getPacketNr()) * 1_000_000_000L;
                    estimatedRTT = (1-alpha) * estimatedRTT + alpha * sampleRTT;
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }).start();


        // logik
        while(true) {

            // window size prüfen
            if(nextSeqNum < base + WINDOW) {
                sendPacket(socket, ipAdress, port, nextSeqNum);
                timestamps.put(nextSeqNum, System.nanoTime());
                nextSeqNum++;
            }

            // receive prüfen
            while(received.contains(base)) {
                base++;
            }

            // abgelaufene timeouts prüfen
            if(base < nextSeqNum) {
                long currentTimestamp = timestamps.get(base);
                if(System.nanoTime() - currentTimestamp > estimatedRTT) {
                    System.out.println("Timeout des Packets Nummer " + base);
                    for(int i = base; i < nextSeqNum; i++) {
                        sendPacket(socket, ipAdress, port, i);
                        timestamps.put(i, System.nanoTime());
                    }
                }
            }
        }
    }


    private void sendPacket(DatagramSocket socket, InetAddress ipAdress, int port, int num) {
        try {
            byte[] send = GbnSimpleCodec.encode(
                    new GbnPing(0, System.nanoTime(), num)
            );
            DatagramPacket p = new DatagramPacket(send, send.length, ipAdress, port);
            socket.send(p);
            System.out.println("Sende Packet Nummer " + num);
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws Exception {
        new Thread(new GoBackNClient()).start();
    }

    private double calculateDuration(int packetNr) {
        Long packetSendTimestamp = timestamps.get(packetNr);
        if(packetSendTimestamp == null){
            System.err.println("ACK aus anderer Session. Kein Timestamp für Packet "+packetNr+" vorhanden.\n");
            return -1;
        }
        long durationNs = System.nanoTime() - packetSendTimestamp;
        double durationMs = durationNs / 1_000_000.0;
        return durationMs/1000;
    }

}
