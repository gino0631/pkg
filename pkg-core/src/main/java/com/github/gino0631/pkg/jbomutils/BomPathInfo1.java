package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;

/**
 * @author JPEXS
 */
public class BomPathInfo1 implements WritableTo {
    public static final int size_of = 2 * Tools.sizeof_uint32_t;

    final long id;
    final long index; // Pointer to BOMPathInfo2

    public BomPathInfo1(long id, long index) {
        this.id = id;
        this.index = index;
    }

    public BomPathInfo1(BomInputStream bis) throws IOException {
        id = bis.readUI32();
        index = bis.readUI32();
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.writeUI32(id);
        bos.writeUI32(index);
    }
}
