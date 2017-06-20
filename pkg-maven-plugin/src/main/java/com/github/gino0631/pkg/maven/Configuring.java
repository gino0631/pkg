package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;

interface Configuring {
    void configure(ProductBuilder pkgBuilder) throws MojoExecutionException;
}
