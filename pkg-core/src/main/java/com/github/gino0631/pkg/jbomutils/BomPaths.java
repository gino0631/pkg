package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JPEXS
 */
public class BomPaths implements WritableTo {
    final int isLeaf; //uint16_t
    final int count; //uint16_t
    long forward;
    final long backward;
    final List<BomPathIndices> indices;

    public BomPaths(int isLeaf, int count, long forward, long backward) {
        this.isLeaf = isLeaf;
        this.count = count;
        this.forward = forward;
        this.backward = backward;
        indices = new ArrayList<>();
    }

    public BomPaths(BomInputStream bis) throws IOException {
        isLeaf = bis.readUI16();
        count = bis.readUI16();
        forward = bis.readUI32();
        backward = bis.readUI32();
        indices = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            indices.add(new BomPathIndices(bis));
        }
    }

    public void setForward(long forward) {
        this.forward = forward;
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.writeUI16(isLeaf);
        bos.writeUI16(count);
        bos.writeUI32(forward);
        bos.writeUI32(backward);
        for (BomPathIndices i : indices) {
            bos.write(i);
        }
    }
}
