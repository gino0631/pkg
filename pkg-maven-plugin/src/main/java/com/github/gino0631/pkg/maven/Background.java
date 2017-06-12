package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.core.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public class Background extends Multilingual {
    @Parameter
    private String alignment;

    @Parameter
    private String scaling;

    void configure(ProductBuilder pkgBuilder) throws MojoExecutionException {
        pkgBuilder.setBackground(getFileName("background"), alignment, scaling, getFiles());
    }
}
