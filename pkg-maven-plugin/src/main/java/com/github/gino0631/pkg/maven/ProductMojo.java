package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.core.FilePermissions;
import com.github.gino0631.pkg.core.ProductBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Mojo(name = "product", defaultPhase = LifecyclePhase.PACKAGE)
public class ProductMojo extends AbstractMojo {
    /**
     * The directory containing payload to install.
     */
    @Parameter(required = true)
    private File root;

    /**
     * The directory where the package will be created.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File buildDirectory;

    /**
     * Output file name.
     */
    @Parameter(defaultValue = "${project.artifactId}-${project.version}.pkg", required = true)
    private String outputFile;

    /**
     * Package identifier. Should be unique and conform to Uniform Type Identifier syntax.
     */
    @Parameter(defaultValue = "${project.groupId}.${project.artifactId}.pkg", required = true)
    private String packageIdentifier;

    /**
     * Package version.
     */
    @Parameter(defaultValue = "${project.artifact.selectedVersion.majorVersion}.${project.artifact.selectedVersion.minorVersion}", required = true)
    private String packageVersion;

    /**
     * Package title.
     */
    @Parameter(defaultValue = "${project.name}", required = true)
    private String packageTitle;

    /**
     * Distribution definition.
     */
    @Parameter
    private Distribution distribution;

    /**
     * File permissions.
     */
    @Parameter
    private List<PermissionSet> permissionSets;

    /**
     * Signing options.
     */
    @Parameter
    private Signing signing;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final Path target = buildDirectory.toPath();

            ProductBuilder pkgBuilder = new ProductBuilder();
            pkgBuilder.setRootDir(root.toPath());
            pkgBuilder.setPkgFile(target.resolve(outputFile));
            pkgBuilder.setIdentifier(packageIdentifier);
            pkgBuilder.setVersion(packageVersion);
            pkgBuilder.setTitle(packageTitle);

            if (permissionSets != null) {
                pkgBuilder.setPermissionSupplier((name, isDirectory) -> {
                    int mode = isDirectory ? FilePermissions.DEFAULT_DIRECTORY_MODE : FilePermissions.DEFAULT_FILE_MODE;
                    int uid = FilePermissions.DEFAULT_UID;
                    int gid = FilePermissions.DEFAULT_GID;

                    for (PermissionSet p : permissionSets) {
                        if (p.matches(name)) {
                            mode = notNull(isDirectory ? p.getDirectoryMode() : p.getFileMode(), mode);
                            uid = notNull(p.getUid(), uid);
                            gid = notNull(p.getGid(), gid);
                        }
                    }

                    return new FilePermissions(mode, uid, gid);
                });
            }

            if (distribution != null) {
                distribution.configure(pkgBuilder);
            }

            if (signing != null) {
                signing.configure(pkgBuilder);
            }

            pkgBuilder.build(target.resolve("pkg-flat"));

        } catch (IOException e) {
            throw new MojoExecutionException("Error building package", e);
        }
    }

    private static int notNull(Integer value, int defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
