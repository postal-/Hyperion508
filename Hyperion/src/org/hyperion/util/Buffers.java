package org.hyperion.util;

import java.nio.ByteBuffer;

public class Buffers {

    /**
     * Reads a null terminated string from a byte buffer.
     *
     * @param buffer The buffer.
     * @return The string.
     */
    public static String readString(ByteBuffer buffer) {
        final StringBuilder bldr = new StringBuilder();
        while (buffer.hasRemaining()) {
            final byte b = buffer.get();
            if (b == 0) {
                break;
            }
            bldr.append((char) b);
        }
        return bldr.toString();
    }
}
