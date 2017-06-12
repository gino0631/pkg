package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;

/**
 *
 * @author JPEXS
 */
public interface WritableTo {

    public void writeTo(BomOutputStream bos) throws IOException;
}
