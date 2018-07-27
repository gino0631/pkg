package com.github.gino0631.pkg.jbomutils;

import java.io.*;
import java.util.*;

/**
 * @author JPEXS
 */
public class MkBom {

    private static int dec_octal_to_int(int dec_rep_octal) {
        int retval = 0;
        for (int n = 1; dec_rep_octal > 0; n *= 8) {
            int digit = dec_rep_octal - ((dec_rep_octal / 10) * 10);
            if (digit > 7) {
                throw new RuntimeException("argument not in dec oct rep");
            }
            retval += digit * n;
            dec_rep_octal /= 10;
        }
        return retval;
    }

    private static class Pair<A, B> {
        final A first;
        final B second;

        Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    public static void write_bom(InputStream is, String outputPath) throws IOException {
        try (OutputStream os = new FileOutputStream(outputPath)) {
            write_bom(is, os);
        }
    }

    public static void write_bom(InputStream is, OutputStream os) throws IOException {
        final Node root = new Node();
        final int num;
        root.type = TNodeType.KRootNode;
        {
            Map<String, Node> all_nodes = new TreeMap<>();
            String line;
            while ((line = Tools.getline(is)) != null) {
                Node n = new Node();
                InputStream ss = new ByteArrayInputStream(line.getBytes());

                String name = Tools.getline(ss, '\t');
                if (name == null) {
                    throw new RuntimeException("Syntax error in lsbom input");
                }
                String[] elements;
                String rest = Tools.getline(ss);
                rest = rest.replaceFirst("/", " ");
                elements = rest.split("\\s");
                n.mode = dec_octal_to_int(Integer.parseInt(elements[0]));
                n.uid = Integer.parseInt(elements[1]);
                n.gid = Integer.parseInt(elements[2]);
                n.size = 0;
                n.checksum = 0;
                //n.linkNameLength = 0;
                if ((n.mode & 0xF000) == 0x4000) {
                    n.type = TNodeType.KDirectoryNode;
                    n.linkName = "";
                } else if ((n.mode & 0xF000) == 0x8000) {
                    n.type = TNodeType.KFileNode;
                    n.size = Integer.parseInt(elements[3]);
                    n.checksum = Long.parseLong(elements[4]);
                    n.linkName = "";
                } else if ((n.mode & 0xF000) == 0xA000) {
                    n.type = TNodeType.KSymbolicLinkNode;
                    n.size = Integer.parseInt(elements[3]);
                    n.checksum = Long.parseLong(elements[4]);
                    //n.linkNameLength = elements[5].length() + 1;
                    n.linkName = elements[5];
                } else {
                    throw new RuntimeException("Node type not supported");
                }
                all_nodes.put(name, n);
            }
            for (String name : all_nodes.keySet()) {
                List<String> path_elements = new ArrayList<>();
                ByteArrayInputStream ss = new ByteArrayInputStream(name.getBytes());
                String element;
                while ((element = Tools.getline(ss, '/')) != null) {
                    path_elements.add(element);
                }
                Node parent = root;
                String full_path = "";
                for (String jt : path_elements) {
                    full_path += jt;
                    if (!parent.children.containsKey(jt)) {
                        if (!all_nodes.containsKey(full_path)) {
                            throw new RuntimeException("Parent directory of file/folder \"" + full_path + "\" does not appear in list");
                        }
                        parent.children.put(jt, all_nodes.get(full_path));
                    }
                    parent = parent.children.get(jt);
                    full_path += "/";
                }
            }
            num = all_nodes.size();
        }

        BomStorage bom = new BomStorage();
        {
            BomInfo info = new BomInfo(1, num + 1);
            if (num != 0) {
                info.entries.add(new BomInfoEntry());
            }
            bom.addVar("BomInfo", info);
        }

        {
            int num_paths = (int) Math.ceil(num / 256.0);
            BomPaths root_paths = new BomPaths(0, num_paths, 0, 0);

            Deque<Pair<Long, Node>> stack = new LinkedList<>();

            stack.push(new Pair<>(0L, root));
            int j = 0;
            int k = 0;
            int last_file_info = 0;
            int last_paths_id = 0;
            BomPaths paths = null;
            while (stack.size() != 0) {
                Pair<Long, Node> p = stack.removeFirst();
                Node arg = p.second;
                long parent = p.first;
                for (Map.Entry<String, Node> e : arg.children.entrySet()) {
                    final String s = e.getKey();
                    final Node node = e.getValue();
                    if (k == 0) {
                        int new_paths_id = 0;
                        if (paths != null) {
                            new_paths_id = bom.addBlock(paths);
                            root_paths.indices.add(new BomPathIndices(new_paths_id, last_file_info));
                            if (last_paths_id != 0) {
                                BomPaths prev_paths = (BomPaths) bom.getBlock(last_paths_id);
                                prev_paths.setForward(new_paths_id);
                            }
                        }

                        int next_num = 256 < (num - j) ? 256 : (num - j);
                        paths = new BomPaths(1, next_num, 0, new_paths_id);
                        last_paths_id = new_paths_id;
                    }

                    BomPathInfo2 info2 = new BomPathInfo2();
                    if (node.type == TNodeType.KDirectoryNode) {
                        info2.type = TYPE.DIR;
                    } else if (node.type == TNodeType.KFileNode) {
                        info2.type = TYPE.FILE;
                    } else {
                        info2.type = TYPE.LINK;
                    }
                    info2.unknown0 = 1;
                    info2.architecture = 3; /* ?? */

                    info2.mode = (int) node.mode;
                    info2.user = node.uid;
                    info2.group = node.gid;
                    info2.modtime = 0;
                    info2.size = node.size;
                    info2.unknown1 = 1;
                    info2.checksum_devType = node.checksum;
                    //info2.linkNameLength = node.linkNameLength;
                    info2.linkName = node.linkName;

                    j++;
                    BomPathInfo1 info1 = new BomPathInfo1(j, bom.addBlock(info2));

                    paths.indices.add(new BomPathIndices(
                            bom.addBlock(info1),
                            last_file_info = bom.addBlock(new BomFile(parent, s))
                    ));

                    stack.addLast(new Pair<>((long) j, node));
                    k = (k + 1) % 256;
                }
            }

            int child;

            if (num_paths > 1) {
                child = bom.addBlock(paths);
                ((BomPaths) bom.getBlock(last_paths_id)).setForward(child);

                root_paths.indices.add(new BomPathIndices(child, last_file_info));
                child = bom.addBlock(root_paths);

            } else {
                child = bom.addBlock(paths);
            }

            bom.addVar("Paths", new BomTree(child, 4096, num));
        }

        {
            BomPaths empty_path = new BomPaths(1, 0, 0, 0);
            bom.addVar("HLIndex", new BomTree(bom.addBlock(empty_path), 4096, 0));

            BomVIndex vindex = new BomVIndex(bom.addBlock(new BomTree(bom.addBlock(empty_path), 128, 0)));
            bom.addVar("VIndex", vindex);

            bom.addVar("Size64", new BomTree(bom.addBlock(empty_path), 4096, 0));
        }
        bom.write(os);
    }

    public static void usage() {
        System.out.println("Usage: java -jar bomutils.jar mkbom [-i] [-u uid] [-g gid] source target-bom-file");
        System.out.println("\t-i\tTreat source as a file in the format generated by ls4mkbom and lsbom");
        System.out.println("\t-u\tForce user ID to the specified value (incompatible with -i)");
        System.out.println("\t-g\tForce group ID to the specified value (incompatible with -i)");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long uid = Long.MAX_VALUE;
        long gid = Long.MAX_VALUE;
        boolean isFileListSource = false;
        int i;
        loopi:
        for (i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-i":
                    isFileListSource = true;
                    break;
                case "-u":
                    uid = Long.parseLong(args[i + 1]);
                    i++;
                    break;
                case "-g":
                    gid = Long.parseLong(args[i + 1]);
                    i++;
                    break;
                case "-h":
                    usage();
                    System.exit(0);
                case "-:":
                case "-?":
                    usage();
                    System.exit(1);
                default:
                    break loopi;
            }
        }

        if (i + 2 > args.length) {
            usage();
            System.exit(1);
        }

        String source = args[i];
        String target_bom = args[i + 1];

        if (isFileListSource) {

            FileInputStream file_list = null;
            try {
                file_list = new FileInputStream(source);
            } catch (IOException ex) {
                System.err.println("Unable to open file list: " + source);
                System.exit(1);
            }
            if ((uid != Long.MAX_VALUE) || (gid != Long.MAX_VALUE)) {
                System.err.println("The -u and -g options cannot be used with -i");
                System.exit(1);
            }
            try {
                write_bom(file_list, target_bom);
            } catch (IOException ex) {
                System.exit(1);
            }
        } else {
            String buffer;
            {
                ByteArrayOutputStream ss = new ByteArrayOutputStream();
                PrintNode.print_node(ss, source, uid, gid);
                buffer = new String(ss.toByteArray());
            }
            ByteArrayInputStream file_list = new ByteArrayInputStream(buffer.getBytes());

            try {
                write_bom(file_list, target_bom);
            } catch (IOException ex) {
                System.exit(1);
            }
        }
        System.exit(0);
    }

}
