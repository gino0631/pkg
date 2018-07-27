package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;

/**
 * @author JPEXS
 */
public class BomVIndex implements WritableTo {
    public static final int size_of = Tools.sizeof_uint32_t * 3 + 1;

    final long unknown0;      // Always 1
    final long indexToVTree;
    final long unknown2;      // Always 0
    /*byte*/
    final int unknown3;       // Always 0

    public BomVIndex(long indexToVTree) {
        this.unknown0 = 1;
        this.indexToVTree = indexToVTree;
        this.unknown2 = 0;
        this.unknown3 = 0;
    }

    public BomVIndex(BomInputStream bis) throws IOException {
        unknown0 = bis.readUI32();
        indexToVTree = bis.readUI32();
        unknown2 = bis.readUI32();
        unknown3 = bis.read();
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.writeUI32(unknown0);
        bos.writeUI32(indexToVTree);
        bos.writeUI32(unknown2);
        bos.write(unknown3);
    }
}
