package com.github.gino0631.pkg.maven;

import org.apache.maven.plugins.annotations.Parameter;

public class Resource {
    @Parameter
    private String file;

    @Parameter
    private String lang;

    public Resource set(String file) {
        this.file = file;

        return this;
    }

    public String getFile() {
        return file;
    }

    public String getLang() {
        return lang;
    }
}
