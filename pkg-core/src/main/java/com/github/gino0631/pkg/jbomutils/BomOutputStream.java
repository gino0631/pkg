package com.github.gino0631.pkg.jbomutils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author JPEXS
 */
public class BomOutputStream extends DataOutputStream {
    public BomOutputStream(OutputStream out) {
        super(out);
    }

    public void writeUI32(long v) throws IOException {
        writeInt((int) v);
    }

    public void writeUI16(int v) throws IOException {
        writeShort(v);
    }

    public void writeUTF8Z(String s) throws IOException {
        write(s.getBytes(StandardCharsets.UTF_8));
        write(0);
    }

    public void write(WritableTo w) throws IOException {
        w.writeTo(this);
    }
}
