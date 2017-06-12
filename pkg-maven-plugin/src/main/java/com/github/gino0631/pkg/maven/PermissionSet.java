package com.github.gino0631.pkg.maven;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.utils.io.MatchPatterns;

import java.util.List;

public class PermissionSet {
    @Parameter
    private List<String> includes;

    @Parameter
    private List<String> excludes;

    @Parameter
    private Integer fileMode;

    @Parameter
    private Integer directoryMode;

    @Parameter
    private Integer uid;

    @Parameter
    private Integer gid;

    private MatchPatterns includesPatterns;
    private MatchPatterns excludesPatterns;

    public boolean matches(String name) {
        if (includesPatterns == null) {
            if ((includes != null) && !includes.isEmpty()) {
                includesPatterns = MatchPatterns.from(toArray(includes));

            } else {
                includesPatterns = MatchPatterns.from("**");
            }
        }

        if (excludesPatterns == null) {
            if ((excludes != null) && !excludes.isEmpty()) {
                excludesPatterns = MatchPatterns.from(toArray(excludes));

            } else {
                excludesPatterns = MatchPatterns.from();
            }
        }

        return includesPatterns.matches(name, true) && !excludesPatterns.matches(name, true);
    }

    public Integer getFileMode() {
        return fileMode;
    }

    public Integer getDirectoryMode() {
        return directoryMode;
    }

    public Integer getUid() {
        return uid;
    }

    public Integer getGid() {
        return gid;
    }

    private static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }
}
