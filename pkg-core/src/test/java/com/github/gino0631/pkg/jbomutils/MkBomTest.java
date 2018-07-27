package com.github.gino0631.pkg.jbomutils;

import com.github.gino0631.common.io.IoStreams;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;

public class MkBomTest {
    @Test
    public void testMkBom() throws Exception {
        byte[] ourBom;
        byte[] referenceBom;

        ourBom = makeBom(getClass().getResourceAsStream("/empty.bom.txt"));
        referenceBom = readBytes(getClass().getResourceAsStream("/empty.bom.bomutils"));
        writeInt(0x00000248, referenceBom, 0x2e1);  // mkbom from bomutils does not fix pointer address if length is 0 (mkbom.cpp:185)
        assertArrayEquals(referenceBom, ourBom);

        ourBom = makeBom(getClass().getResourceAsStream("/siri.bom.txt"));
        referenceBom = readBytes(getClass().getResourceAsStream("/siri.bom.bomutils"));
        assertArrayEquals(referenceBom, ourBom);

        ourBom = makeBom(getClass().getResourceAsStream("/safari.bom.txt"));
        referenceBom = readBytes(getClass().getResourceAsStream("/safari.bom.bomutils"));
        assertArrayEquals(referenceBom, ourBom);
    }

    private static byte[] makeBom(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MkBom.write_bom(is, baos);

        return baos.toByteArray();
    }

    private static byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IoStreams.copy(is, baos);

        return baos.toByteArray();
    }

    private static void writeInt(int v, byte[] array, int offs) {
        array[offs] = (byte) ((v >>> 24) & 0xff);
        array[offs + 1] = (byte) ((v >>> 16) & 0xff);
        array[offs + 2] = (byte) ((v >>> 8) & 0xff);
        array[offs + 3] = (byte) (v & 0xff);
    }
}
