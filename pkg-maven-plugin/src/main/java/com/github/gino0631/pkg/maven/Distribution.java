package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.core.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public class Distribution {
    @Parameter
    private Background background;

    @Parameter
    private License license;

    void configure(ProductBuilder pkgBuilder) throws MojoExecutionException {
        if (background != null) {
            background.configure(pkgBuilder);
        }

        if (license != null) {
            license.configure(pkgBuilder);
        }
    }
}
