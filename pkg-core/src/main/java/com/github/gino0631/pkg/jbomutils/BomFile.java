package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;

/**
 * @author JPEXS
 */
public class BomFile implements WritableTo {
    final long parent;    // Parent BOMPathInfo1->id
    final String name;

    public BomFile(long parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public BomFile(BomInputStream bis) throws IOException {
        parent = bis.readUI32();
        name = bis.readUTF8Z();
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.writeUI32(parent);
        bos.writeUTF8Z(name);
    }
}
