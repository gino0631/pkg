package com.github.gino0631.pkg.core;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.github.gino0631.pkg.jaxb.ObjectFactory;
import com.github.gino0631.pkg.jaxb.distribution.*;
import com.github.gino0631.pkg.jaxb.packageinfo.*;
import com.github.gino0631.xar.EncodingAlgorithm;
import com.github.gino0631.xar.XarArchive;
import com.github.gino0631.xar.XarBuilder;
import com.github.gino0631.xar.impl.IoUtils;
import com.github.gino0631.pkg.jbomutils.MkBom;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioConstants;

import java.io.*;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import java.util.zip.GZIPOutputStream;

public final class ProductBuilder {
    private Path rootDir;
    private Path pkgFile;
    private String identifier;
    private String version;
    private String title;
    private Background background;
    private License license;
    private Map<String, Map<String, Path>> resources;
    private PermissionSupplier permissionSupplier;
    private PrivateKey signingPrivateKey;
    private List<X509Certificate> signingCertificates;
    private Provider signingProvider;
    private URL signingTsa;

    @FunctionalInterface
    public interface PermissionSupplier {
        FilePermissions get(String name, boolean isDirectory);
    }

    public void setRootDir(Path rootDir) {
        this.rootDir = rootDir;
    }

    public void setPkgFile(Path pkgFile) {
        this.pkgFile = pkgFile;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackground(String name, String alignment, String scaling, Map<String, Path> files) {
        if ((files == null) || files.isEmpty()) {
            throw new IllegalArgumentException("Background files must be specified");
        }

        if (background != null) {
            throw new IllegalStateException("Background is already set");
        }

        background = new Background(Objects.requireNonNull(name), alignment, scaling);
        addResources(name, files);
    }

    public void setLicense(String name, Map<String, Path> files) {
        if ((files == null) || files.isEmpty()) {
            throw new IllegalArgumentException("License files must be specified");
        }

        if (license != null) {
            throw new IllegalStateException("License is already set");
        }

        license = new License(Objects.requireNonNull(name));
        addResources(name, files);
    }

    public void setPermissionSupplier(PermissionSupplier permissionSupplier) {
        this.permissionSupplier = permissionSupplier;
    }

    public void setSigning(PrivateKey signingPrivateKey, List<X509Certificate> signingCertificates, Provider signingProvider, URL signingTsa) {
        this.signingPrivateKey = signingPrivateKey;
        this.signingCertificates = signingCertificates;
        this.signingProvider = signingProvider;
        this.signingTsa = signingTsa;
    }

    public void build(Path flatDir) throws IOException {
        // Validation
        Objects.requireNonNull(rootDir, "Root directory must be specified");
        if (Files.notExists(rootDir)) {
            throw new IllegalStateException(MessageFormat.format("Root directory {0} does not exist", rootDir));
        }

        Objects.requireNonNull(pkgFile, "PKG file must be specified");
        Objects.requireNonNull(identifier, "Package identifier must be specified");
        Objects.requireNonNull(version, "Package version must be specified");
        Objects.requireNonNull(title, "Title must be specified");

        // Cleanup
        if (Files.exists(flatDir)) {
            delete(flatDir);
        }

        // Creation
        Files.createDirectories(flatDir);
        Path basePkgDir = Files.createDirectory(flatDir.resolve("base.pkg"));

        final int rootPathLength = rootDir.toString().length();
        long installBytes = 0;
        int numberOfFiles = 0;
        List<Bundle> bundles = new ArrayList<>();
        ByteArrayOutputStream bomListOS = new ByteArrayOutputStream();
        PrintStream bomListPS = new PrintStream(bomListOS);

        // Collect files
        try (OutputStream os = Files.newOutputStream(basePkgDir.resolve("Payload"))) {
            try (GZIPOutputStream gzip = new GZIPOutputStream(os)) {
                try (CpioArchiveOutputStream cpio = new CpioArchiveOutputStream(gzip, CpioConstants.FORMAT_OLD_ASCII)) {
                    List<Path> files = Files.walk(rootDir).collect(Collectors.toList());

                    for (Path path : files) {
                        final String name = "." + path.toString().substring(rootPathLength).replace('\\', '/');
                        final boolean isDirectory = Files.isDirectory(path);
                        final long size = isDirectory ? 0 : Files.size(path);
                        installBytes += size;
                        numberOfFiles++;

                        if (name.endsWith("/Info.plist")) {
                            if (name.startsWith("./Applications/")) {
                                String bundlePath = name.substring(0, name.indexOf('/', 15));
                                bundles.add(getBundle(bundlePath, path));
                            }
                        }

                        CpioArchiveEntry entry = new CpioArchiveEntry(CpioConstants.FORMAT_OLD_ASCII, name, size);
                        int mode = isDirectory ? FilePermissions.DEFAULT_DIRECTORY_MODE : FilePermissions.DEFAULT_FILE_MODE;
                        int uid = FilePermissions.DEFAULT_UID;
                        int gid = FilePermissions.DEFAULT_GID;

                        if (permissionSupplier != null) {
                            FilePermissions permissions = permissionSupplier.get(name, isDirectory);
                            if (permissions != null) {
                                mode = permissions.getMode();
                                uid = permissions.getUserId();
                                gid = permissions.getGroupId();
                            }
                        }

                        mode |= (isDirectory ? CpioConstants.C_ISDIR : CpioConstants.C_ISREG);

                        entry.setMode(mode);
                        entry.setUID(uid);
                        entry.setGID(gid);
                        entry.setTime(Files.getLastModifiedTime(path).to(TimeUnit.SECONDS));
                        entry.setNumberOfLinks(isDirectory ? Files.list(path).count() + 2 : 1);

                        bomListPS.print(name);
                        bomListPS.print('\t');
                        bomListPS.print(Integer.toOctalString(mode));
                        bomListPS.print('\t');
                        bomListPS.print(uid);
                        bomListPS.print('/');
                        bomListPS.print(gid);

                        cpio.putArchiveEntry(entry);
                        if (!isDirectory) {
                            Checksum cksum = new Cksum();
                            try (InputStream in = new CheckedInputStream(Files.newInputStream(path), cksum)) {
                                IoUtils.copy(in, cpio);
                            }
                            bomListPS.print('\t');
                            bomListPS.print(size);
                            bomListPS.print('\t');
                            bomListPS.print(cksum.getValue());
                        }
                        cpio.closeArchiveEntry();

                        bomListPS.println();
                    }
                }
            }
        }

        // Write Bom
        bomListPS.close();
        MkBom.write_bom(new ByteArrayInputStream(bomListOS.toByteArray()), basePkgDir.resolve("Bom").toString());

        final Integer installKBytes = (int) Math.ceil(installBytes / 1024d);

        // Write PackageInfo
        try (OutputStream os = Files.newOutputStream(basePkgDir.resolve("PackageInfo"))) {
            PkgInfo pkgInfo = new PkgInfo(identifier, Auth.ROOT, version, "/",
                    new Payload(installKBytes, numberOfFiles - 1));
            pkgInfo.setBundleVersion(new BundleVersion(bundles));

            ObjectFactory.marshal(pkgInfo, os);
        }

        // Write Distribution
        try (OutputStream os = Files.newOutputStream(flatDir.resolve("Distribution"))) {
            InstallerScript installerScript = new InstallerScript(title,
                    new Options(Options.Customize.NEVER, false),
                    new ChoicesOutline(Collections.singletonList(new Line("default"))),
                    Collections.singletonList(new Choice("default", Collections.singletonList(new PkgRef(identifier)))),
                    Collections.singletonList(new PkgRef(identifier, version, installKBytes, "#base.pkg")));

            installerScript.setBackground(background);
            installerScript.setLicense(license);

            ObjectFactory.marshal(installerScript, os);
        }

        // Build XAR
        try (XarBuilder xarBuilder = XarBuilder.getInstance()) {
            xarBuilder.setSigning(signingPrivateKey, signingCertificates, signingProvider, signingTsa);

            Files.walkFileTree(flatDir, new SimpleFileVisitor<Path>() {
                Deque<XarBuilder.Container> xarPath = new LinkedList<>();

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    FileVisitResult res = super.preVisitDirectory(dir, attrs);

                    if (dir.equals(flatDir)) {
                        xarPath.push(xarBuilder.getRoot());

                    } else {
                        XarBuilder.Directory directory = xarPath.element().addDirectory(dir.getFileName().toString());
                        xarPath.push(directory);
                    }

                    return res;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    FileVisitResult res = super.postVisitDirectory(dir, exc);

                    xarPath.pop();

                    return res;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    FileVisitResult res = super.visitFile(file, attrs);

                    String fileName = file.getFileName().toString();
                    EncodingAlgorithm encodingAlgorithm = fileName.equals("Payload") ? EncodingAlgorithm.NONE : EncodingAlgorithm.ZLIB;

                    try (InputStream is = Files.newInputStream(file)) {
                        xarPath.element().addFile(fileName, encodingAlgorithm, is);
                    }

                    return res;
                }
            });

            if (resources != null) {
                XarBuilder.Directory resourceDir = xarBuilder.getRoot().addDirectory("Resources");
                for (Map.Entry<String, Map<String, Path>> langFiles : resources.entrySet()) {
                    String lang = langFiles.getKey();
                    XarBuilder.Directory langDir = lang.isEmpty() ? resourceDir : resourceDir.addDirectory(lang + ".lproj");

                    for (Map.Entry<String, Path> e : langFiles.getValue().entrySet()) {
                        try (InputStream is = Files.newInputStream(e.getValue())) {
                            langDir.addFile(e.getKey(), EncodingAlgorithm.ZLIB, is);
                        }
                    }
                }
            }

            try (XarArchive xar = xarBuilder.build()) {
                try (OutputStream os = Files.newOutputStream(pkgFile)) {
                    xar.writeTo(os);
                }
            }
        }
    }

    private void addResources(String name, Map<String, Path> files) {
        if (resources == null) {
            resources = new TreeMap<>();
        }

        for (Map.Entry<String, Path> e : files.entrySet()) {
            String lang = e.getKey();
            if (lang == null) {
                lang = "";
            }
            resources.computeIfAbsent(lang, k -> new TreeMap<>()).put(name, e.getValue());
        }
    }

    private static void delete(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static Bundle getBundle(String path, Path infoPlist) {
        try {
            NSDictionary dict = (NSDictionary) PropertyListParser.parse(infoPlist.toFile());
            String cfBundleIdentifier = getString(dict, "CFBundleIdentifier");
            String cfBundleVersion = getString(dict, "CFBundleVersion");

            return new Bundle(path, cfBundleIdentifier, cfBundleIdentifier, cfBundleVersion);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getString(NSDictionary dict, String name) {
        return dict.get(name).toJavaObject().toString();
    }
}
