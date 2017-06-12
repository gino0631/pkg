package com.github.gino0631.pkg.core;

import java.text.MessageFormat;

@SuppressWarnings("OctalInteger")
public final class FilePermissions {
    public static final int DEFAULT_FILE_MODE = 0644;
    public static final int DEFAULT_DIRECTORY_MODE = 0755;
    public static final int DEFAULT_UID = 0;
    public static final int DEFAULT_GID = 0;

    private static final int MODE_MASK = ~0777;

    private final int mode;
    private final int uid;
    private final int gid;

    public FilePermissions(int mode) {
        this(mode, DEFAULT_UID, DEFAULT_GID);
    }

    public FilePermissions(int mode, int uid, int gid) {
        if ((mode & MODE_MASK) != 0) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal mode {0}", Integer.toOctalString(mode)));
        }

        this.mode = mode;
        this.uid = uid;
        this.gid = gid;
    }

    public int getMode() {
        return mode;
    }

    public int getUserId() {
        return uid;
    }

    public int getGroupId() {
        return gid;
    }
}
