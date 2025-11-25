import java.net.*;
import message.*;

public class PingServer {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(9876);
        System.out.println("[SERVER] Stop-and-Wait Server gestartet auf Port 9876 ...");

        int erwarteteSeq = 0;
        int maxPings = 2;
        int processed = 0;

        try {

            while (processed < maxPings) {

                byte[] recvData = new byte[1024];
                DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);

                System.out.println("\n[SERVER] Zustand: WARTE_AUF_ANFRAGE(PING)_" + erwarteteSeq);

                socket.receive(recvPacket);

                Message msg;

                try {
                    msg = SimpleCodec.decode(recvPacket.getData(), recvPacket.getLength());
                } catch (Exception e) {
                    System.out.println("[SERVER] Prüfsummenfehler - beschädigtes Paket ignoriert");
                    continue;
                }

                if (msg.getType() == MsgType.PING) {

                    System.out.println("[SERVER] Ping erhalten: " + msg);

                    int seq = msg.getSeq();
                    InetAddress clientIP = recvPacket.getAddress();
                    int clientPort = recvPacket.getPort();

                    SimpleMessage pong = new SimpleMessage(MsgType.PONG, seq);
                    byte[] sendData = SimpleCodec.encode(pong);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, clientPort);

                    if (seq == erwarteteSeq) {
                        System.out.println("[SERVER] Richtige Sequenznummer erhalten: " + seq);
                        System.out.println("[SERVER] Verarbeite Ping(" + seq + ")");
                        System.out.println("[SERVER] Sende Pong(" + seq + ")");

                        socket.send(sendPacket);

                        erwarteteSeq = 1 - erwarteteSeq;
                        processed++;

                        System.out.println("[SERVER] Wechsel zu WARTE_AUF_PING_" + erwarteteSeq);
                        System.out.println("[SERVER] Verarbeitet: " + processed + "/" + maxPings);

                    } else {
                        System.out.println("[SERVER] Duplikat erkannt oder falsches Seq (seq=" + seq + ")");
                        System.out.println("[SERVER] Paket ignoriert / kein Pong gesendet");
                    }

                } else {
                    System.out.println("[SERVER] Unerwarteter Nachrichtentyp: " + msg.getType());
                }
            }

        } finally {
            System.out.println("\n[SERVER] Maximalzahl der Pings erreicht. Server wird geschlossen...");
            socket.close();
        }

        System.out.println("[SERVER] Socket geschlossen. Server beendet.");
    }
}
