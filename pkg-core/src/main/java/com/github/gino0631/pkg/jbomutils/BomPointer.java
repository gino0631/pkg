package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;

/**
 *
 * @author JPEXS
 */
public class BomPointer implements WritableTo {

    public static final int sizeof = 8;

    long address;
    long length;

    public BomPointer() {
    }

    public BomPointer(BomInputStream bis) throws IOException {
        address = bis.readUI32();
        length = bis.readUI32();
    }

    @Override
    protected BomPointer clone() {
        try {
            return (BomPointer) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.writeUI32(address);
        bos.writeUI32(length);
    }

}
