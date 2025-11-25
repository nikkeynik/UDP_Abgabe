package gbnMessage;

import java.nio.charset.StandardCharsets;

import static gbnMessage.GbnMsgType.PING;
import static gbnMessage.GbnMsgType.PONG;


public final class GbnSimpleCodec {

    public static byte[] encode(GbnMessage msg) {
        String s = String.format(
            "type=%s;seq=%d;tm=%d;pnr=%d",
            msg.getType(),
            msg.getSeq(),
            msg.getTime(),
            msg.getPacketNr()
        );
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static GbnMessage decode(byte[] bytes) {
        String s = new String(bytes, StandardCharsets.UTF_8);
        String[] parts = s.split(";");

        String type = null;
        int seq = 0;
        long ts = 0;
        int pnr = 0;

        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2) continue;

            switch (kv[0]) {
                case "type" -> type = kv[1];
                case "seq" -> seq = Integer.parseInt(kv[1]);
                case "tm" -> ts =  Long.parseLong(kv[1].trim());
                case "pnr" -> pnr = Integer.parseInt(kv[1].trim());
            }
        }

        if (type == null)
            throw new IllegalArgumentException("Missing type in message: " + s);

        switch (type) {
            case "PING":
                return new GbnSimpleMessage(PING, seq, ts, pnr);
            case "PONG":
                return new GbnSimpleMessage(PONG, seq, ts, pnr);
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}