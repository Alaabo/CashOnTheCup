package dz.alaabo.encoding;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class CompressedString implements Serializable {
    private final byte[] encoded;

    public CompressedString(byte[] encoded) {
        this.encoded = encoded;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(4 + encoded.length);
        buffer.putInt(encoded.length);
        buffer.put(encoded);
        return buffer.array();
    }

    public static CompressedString fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int length = buffer.getInt();
        byte[] data = new byte[length];
        buffer.get(data);
        return new CompressedString(data);
    }

    public byte[] getEncoded() {
        return encoded;
    }
}
