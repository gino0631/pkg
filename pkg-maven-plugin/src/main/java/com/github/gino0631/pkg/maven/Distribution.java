package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public class Distribution implements Configuring {
    @Parameter
    private Background background;

    @Parameter
    private License license;

    @Parameter
    private Readme readme;

    @Parameter
    private Conclusion conclusion;

    @Override
    public void configure(ProductBuilder pkgBuilder) throws MojoExecutionException {
        ProductMojo.configure(pkgBuilder, background);
        ProductMojo.configure(pkgBuilder, license);
        ProductMojo.configure(pkgBuilder, readme);
        ProductMojo.configure(pkgBuilder, conclusion);
    }
}
