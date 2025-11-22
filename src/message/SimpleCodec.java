package message;
import java.nio.charset.StandardCharsets;


public final class SimpleCodec {

    public static byte[] encode(Message msg) {
        String s = String.format(
             "type=%s;seq=%d;tm=%d;cs=%d",
            msg.getType(),
            msg.getSeq(),
            msg.getTime(),
            msg.getChecksum()
        );
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static Message decode(byte[] bytes) {
        String s = new String(bytes, StandardCharsets.UTF_8);
        String[] parts = s.split(";");

        String type = null;
        int seq = 0;
        long ts = 0;
        int cs = 0;

        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2) continue;

            switch (kv[0]) {
                case "type" -> type = kv[1];
                case "seq" -> seq = Integer.parseInt(kv[1]);
                case "tm" -> ts =  Long.parseLong(kv[1].trim());
                case "cs" -> cs = Integer.parseInt(kv[1].trim());
            }
        }

        if (type == null)
            throw new IllegalArgumentException("Fehlender Typ in der Nachricht: " + s);

        //cheksumme berechnen
        int berechneteChecksum = 0;
        String data = type + seq + ts;
        for (char c : data.toCharArray()) berechneteChecksum += c;
        berechneteChecksum %= 256;

        System.out.println("[CHECKSUMME] Empfangen: " + cs + ", Berechnet: " + berechneteChecksum);

        if (berechneteChecksum != cs) {
            throw new IllegalArgumentException("Prüfsummenfehler! Empfangen: " + cs + ", berechnet : " + berechneteChecksum);
        }

        switch (type) {
            case "PING":
                return new Ping(seq, ts, cs);
            case "PONG":
                return new Pong(seq, ts, cs);
            default:
                throw new IllegalArgumentException("Unbekannter Typ: " + type);
        }
    }
}