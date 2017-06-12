package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.core.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;

public class License extends Multilingual {
    void configure(ProductBuilder pkgBuilder) throws MojoExecutionException {
        pkgBuilder.setLicense(getFileName("license"), getFiles());
    }
}
