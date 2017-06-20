package com.github.gino0631.pkg;

import com.github.gino0631.pkg.jbomutils.Crc32;

import java.util.zip.Checksum;

public final class Cksum implements Checksum {
    private Crc32 crc32;
    private long count;

    public Cksum() {
        reset();
    }

    @Override
    public void update(int b) {
        if (count < 0) {
            throw new IllegalStateException();
        }

        crc32.update(b);
        count++;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        if (count < 0) {
            throw new IllegalStateException();
        }

        for (int i = off; i < off + len; i++) {
            crc32.update(b[i]);
        }

        count += len;
    }

    @Override
    public long getValue() {
        while (count > 0) {
            crc32.update((int) count & 0xff);
            count >>>= 8;
        }
        count = -1;

        return crc32.getValue() ^ 0xffffffffL;
    }

    @Override
    public void reset() {
        crc32 = new Crc32();
        count = 0;
    }
}
