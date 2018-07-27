package com.github.gino0631.pkg.jbomutils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author JPEXS
 */
public class BomInputStream extends DataInputStream {
    public BomInputStream(byte[] data) {
        this(new ByteArrayInputStream(data));
    }

    public BomInputStream(InputStream is) {
        super(is);
    }

    public long readUI32() throws IOException {
        return Integer.toUnsignedLong(readInt());
    }

    public int readUI16() throws IOException {
        return Short.toUnsignedInt(readShort());
    }

    public String readUTF8Z() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int v;
        while ((v = read()) > 0) {
            baos.write(v);
        }

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
