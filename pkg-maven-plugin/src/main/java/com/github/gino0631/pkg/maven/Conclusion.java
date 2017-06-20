package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;

public class Conclusion extends Multilingual implements Configuring {
    @Override
    public void configure(ProductBuilder pkgBuilder) throws MojoExecutionException {
        pkgBuilder.setConclusion(getFileName("conclusion"), getFiles());
    }
}
