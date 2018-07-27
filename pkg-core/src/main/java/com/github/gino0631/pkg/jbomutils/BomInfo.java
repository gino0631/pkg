package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JPEXS
 */
public class BomInfo implements WritableTo {
    final long version;
    final long numberOfPaths;
    final List<BomInfoEntry> entries = new ArrayList<>();

    public BomInfo(long version, long numberOfPaths) {
        this.version = version;
        this.numberOfPaths = numberOfPaths;
    }

    public BomInfo(BomInputStream bis) throws IOException {
        version = bis.readUI32();
        numberOfPaths = bis.readUI32();
        int numberOfInfoEntries = (int) bis.readUI32();
        for (int i = 0; i < numberOfInfoEntries; i++) {
            entries.add(new BomInfoEntry(bis));
        }
    }

    @Override
    public void writeTo(BomOutputStream bos) throws IOException {
        bos.writeUI32(version);
        bos.writeUI32(numberOfPaths);
        bos.writeUI32(entries.size());
        for (BomInfoEntry b : entries) {
            b.writeTo(bos);
        }
    }
}
