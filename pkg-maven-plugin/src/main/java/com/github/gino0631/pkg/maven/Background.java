package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public class Background extends Multilingual implements Configuring {
    @Parameter
    private String alignment;

    @Parameter
    private String scaling;

    @Override
    public void configure(ProductBuilder pkgBuilder) throws MojoExecutionException {
        pkgBuilder.setBackground(getFileName("background"), alignment, scaling, getFiles());
    }
}
