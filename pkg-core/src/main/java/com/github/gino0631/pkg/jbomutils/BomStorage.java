package com.github.gino0631.pkg.jbomutils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JPEXS
 */
public class BomStorage {
    private long size_of_header;

    private long size_of_vars;
    private List<BomVar> vars;

    private long size_of_free_list;
    private List<BomPointer> free_list;

    private List<WritableTo> entries;

    public BomStorage() {
        size_of_header = 512;

        size_of_vars = Tools.sizeof_uint32_t;
        vars = new ArrayList<>();

        entries = new ArrayList<>();

        size_of_free_list = Tools.sizeof_uint32_t + 2 * BomPointer.sizeof;
        free_list = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            free_list.add(new BomPointer(0, 0));
        }
    }

    public WritableTo getBlock(int id) {
        return entries.get(id - 1);
    }

    public int addBlock(WritableTo data) {
        entries.add(data);

        return entries.size();
    }

    public void addVar(String name, WritableTo data) {
        int new_size = Tools.sizeof_uint32_t + 1 + name.length();

        BomVar var = new BomVar();
        size_of_vars += new_size;
        var.index = addBlock(data);
        var.name = name;
        vars.add(var);
    }

    public void write(OutputStream bom_file) throws IOException {
        BomOutputStream bos = new BomOutputStream(bom_file);

        List<BomPointer> bomPointers = new ArrayList<>(entries.size() + 1);
        bomPointers.add(new BomPointer(0, 0));

        long address = size_of_header + size_of_vars;
        List<byte[]> dataBlocks = new ArrayList<>(entries.size());

        for (WritableTo e : entries) {
            if (e != null) {
                byte[] data = e.getBytes();
                dataBlocks.add(data);

                bomPointers.add(new BomPointer(address, data.length));
                address += data.length;

            } else {
                bomPointers.add(new BomPointer(address, 0));
            }
        }

        final long entry_size = address - (size_of_header + size_of_vars);
        final long size_of_block_table = Tools.sizeof_uint32_t + ((entries.size() + 1) * BomPointer.sizeof);

        BomHeader header = new BomHeader();
        header.numberOfBlocks = entries.size();
        header.indexOffset = size_of_header + size_of_vars + entry_size;
        header.indexLength = size_of_block_table + size_of_free_list;
        header.varsOffset = size_of_header;
        header.varsLength = size_of_vars;

        bos.write(header);//size_of_header        

        bos.writeUI32(vars.size());
        for (BomVar v : vars) {
            bos.write(v);
        }

        for (byte[] data : dataBlocks) {
            bos.write(data);
        }

        bos.writeUI32(bomPointers.size());
        for (BomPointer p : bomPointers) {
            bos.write(p);
        }

        bos.writeUI32(free_list.size() - 2);
        for (BomPointer f : free_list) {
            bos.write(f);
        }
    }
}
