package message;
import java.nio.charset.StandardCharsets;

public final class SimpleCodec {

    public byte[] encode(Message msg) {
        String s = String.format(
            "type=%s;seq=%d;tm=%d",
            msg.getType(),
            msg.getSeq(),
            msg.getTime()
        );
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public Message decode(byte[] bytes) {
        String s = new String(bytes, StandardCharsets.UTF_8);
        String[] parts = s.split(";");

        String type = null;
        int seq = 0;
        long ts = 0;

        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2) continue;

            switch (kv[0]) {
                case "type" -> type = kv[1];
                case "seq" -> seq = Integer.parseInt(kv[1]);
                case "tm" -> ts = Long.parseLong(kv[1]);
            }
        }

        if (type == null)
            throw new IllegalArgumentException("Missing type in message: " + s);

        switch (type) {
            case "PING":
                return new Ping(seq, ts);
            case "PONG":
                return new Pong(seq, ts);
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}