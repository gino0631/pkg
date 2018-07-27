package com.github.gino0631.pkg.jbomutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author JPEXS
 */
public interface WritableTo {
    void writeTo(BomOutputStream bos) throws IOException;

    default byte[] getBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BomOutputStream bos = new BomOutputStream(baos);
            writeTo(bos);

            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);  // writing to byte arrays should be safe
        }
    }
}
