package com.github.gino0631.pkg.core;

import org.apache.commons.compress.utils.Charsets;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class CksumTest {
    @Test
    public void test() throws Exception {
        Cksum cksum = new Cksum();
        cksum.update("12345".getBytes(Charsets.UTF_8), 0, 5);
        assertEquals(3288622155L, cksum.getValue());

        cksum.reset();
        byte[] buf = new byte[3072 * 1024];
        Arrays.fill(buf, (byte) 7);
        for (int i = 0; i < 1024; i++) {
            cksum.update(buf, 0, buf.length);
        }
        assertEquals(1119591825L, cksum.getValue());
    }
}
