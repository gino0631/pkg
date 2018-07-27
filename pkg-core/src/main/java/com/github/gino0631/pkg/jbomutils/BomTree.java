package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;

/**
 * @author JPEXS
 */
public class BomTree implements WritableTo {
    public static final int size_of = 4 + 4 * Tools.sizeof_uint32_t + 1;

    final byte[] tree = "tree".getBytes();    // Always "tree"
    final long version;     // Always 1
    final long child;       // Index for BOMPaths
    final long blockSize;   // Always 4096
    final long pathCount;   // Total number of paths in all leaves combined
    /*byte*/
    final int unknown3;

    public BomTree(long child, long blockSize, long pathCount) {
        this.version = 1;
        this.child = child;
        this.blockSize = blockSize;
        this.pathCount = pathCount;
        this.unknown3 = 0;
    }

    public BomTree(BomInputStream bis) throws IOException {
        bis.readFully(tree);
        version = bis.readUI32();
        child = bis.readUI32();
        blockSize = bis.readUI32();
        pathCount = bis.readUI32();
        unknown3 = bis.read();
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.write(tree);
        bos.writeUI32(version);
        bos.writeUI32(child);
        bos.writeUI32(blockSize);
        bos.writeUI32(pathCount);
        bos.write(unknown3);
    }
}
