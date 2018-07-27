package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;

/**
 * @author JPEXS
 */
public class BomPathIndices implements WritableTo {
    public static final int size_of = 2 * Tools.sizeof_uint32_t;

    final long index0; /* for leaf: points to BOMPathInfo1, for branch points to BOMPaths */
    final long index1; /* always points to BOMFile */

    public BomPathIndices(long index0, long index1) {
        this.index0 = index0;
        this.index1 = index1;
    }

    public BomPathIndices(BomInputStream bis) throws IOException {
        index0 = bis.readUI32();
        index1 = bis.readUI32();
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.writeUI32(index0);
        bos.writeUI32(index1);
    }
}
