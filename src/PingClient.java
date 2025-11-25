import java.net.*;
import message.*;

public class PingClient {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        InetAddress serverIP = InetAddress.getByName("localhost");
        int serverPort = 9876;

        int seq = 0;

        System.out.println("[CLIENT] Starte Stop-and-Wait Client...");

        // Ping(0) korrekt
        SimpleMessage ping1 = new SimpleMessage(MsgType.PING, seq);
        byte[] sendData1 = SimpleCodec.encode(ping1);

        DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, serverIP, serverPort);
        System.out.println("\n[CLIENT] Zustand: WARTE_AUF_AUFRUF " + seq);
        System.out.println("[CLIENT] SENDE_PING_(" + seq + ") ...");
        socket.send(sendPacket1);

        boolean korrektesPongErhalten = false;
        while (!korrektesPongErhalten) {
            try {
                byte[] recvData = new byte[1024];
                DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
                System.out.println("[CLIENT] WARTE_AUF_PONG(" + seq + ") ...");
                socket.receive(recvPacket);

                Message msg = SimpleCodec.decode(recvPacket.getData(), recvPacket.getLength());
                if (msg.getType() == MsgType.PONG && msg.getSeq() == seq) {
                    System.out.println("[CLIENT] Korrektes Pong(" + seq + ") erhalten.");
                    korrektesPongErhalten = true;
                }
            } catch (SocketTimeoutException e) {
                System.out.println("[CLIENT] Timeout! Sende Ping(" + seq + ") erneut...");
                socket.send(sendPacket1);
            }
        }

        // seq wechseln
        seq = 1 - seq;

        // 2 Ping – beschädigt + retry
        SimpleMessage ping2 = new SimpleMessage(MsgType.PING, seq);
        byte[] sendData2 = SimpleCodec.encode(ping2);
        sendData2[0] ^= 0xFF;  // beschädigen

        System.out.println("\n[CLIENT] Packet ist beschädigt");
        DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, serverIP, serverPort);

        boolean pongFuerBeschaedigt = false;
        int versuche = 0;
        int MAX_VERSUCHE = 3;

        while (!pongFuerBeschaedigt && versuche < MAX_VERSUCHE) {
            System.out.println("[CLIENT] SENDE_PING_(" + seq + ") (beschädigt) ...");
            socket.send(sendPacket2);

            try {
                byte[] recvData = new byte[1024];
                DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
                System.out.println("[CLIENT] WARTE_AUF_PONG(" + seq + ") ...");
                socket.receive(recvPacket);

                Message msg = SimpleCodec.decode(recvPacket.getData(), recvPacket.getLength());
                if (msg.getType() == MsgType.PONG) {
                    System.out.println("[CLIENT] Pong erhalten (unerwartet!): " + msg);
                    pongFuerBeschaedigt = true;
                }

            } catch (SocketTimeoutException e) {
                System.out.println("[CLIENT] Timeout! Kein Pong erhalten. Versuch " + (versuche + 1) + "/" + MAX_VERSUCHE);
                versuche++;
            }
        }

        if (!pongFuerBeschaedigt)
            System.out.println("[CLIENT] Bestätigt: beschädigtes Ping(" + seq + ") führt zu keinem Pong.");

        // Duplikat
        System.out.println("\n[CLIENT] Sende absichtlich DUPLIKAT Ping(" + seq + ") ...");
        socket.send(sendPacket2);

        try {
            byte[] recvData = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
            socket.receive(recvPacket);
        } catch (SocketTimeoutException e) {
            System.out.println("[CLIENT] Kein Pong auf Duplikat erhalten (korrektes Verhalten).");
        }

        // PING(1) korrekt
        SimpleMessage ping2_ok = new SimpleMessage(MsgType.PING, seq);
        byte[] sendData2_ok = SimpleCodec.encode(ping2_ok);

        DatagramPacket sendPacket2_ok = new DatagramPacket(sendData2_ok, sendData2_ok.length, serverIP, serverPort);
        System.out.println("\n[CLIENT] SENDE_PING_(" + seq + ") (korrekt) ...");
        socket.send(sendPacket2_ok);

        try {
            byte[] recvData = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
            System.out.println("[CLIENT] WARTE_AUF_PONG(" + seq + ") ...");
            socket.receive(recvPacket);

            Message msg = SimpleCodec.decode(recvPacket.getData(), recvPacket.getLength());
            if (msg.getType() == MsgType.PONG) {
                System.out.println("[CLIENT] Korrektes Pong erhalten: " + msg);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[CLIENT] Fehler: Server hätte jetzt antworten müssen!");
        }

        System.out.println("\n[CLIENT] Client beendet.");
        socket.close();
    }
}
