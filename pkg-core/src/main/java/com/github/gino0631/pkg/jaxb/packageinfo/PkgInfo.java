package com.github.gino0631.pkg.jaxb.packageinfo;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "pkg-info")
public class PkgInfo {
    @XmlAttribute(name = "format-version", required = true)
    protected String formatVersion = "2";

    @XmlAttribute(name = "identifier", required = true)
    protected String identifier;

    @XmlAttribute(name = "auth", required = true)
    protected Auth auth;

    @XmlAttribute(name = "version", required = true)
    protected String version;

    @XmlAttribute(name = "install-location")
    protected String installLocation;

    @XmlElement(name = "payload", required = true)
    protected Payload payload;

    @XmlElement(name = "bundle-version")
    protected BundleVersion bundleVersion;

    public PkgInfo() {
    }

    public PkgInfo(String identifier, Auth auth, String version, String installLocation, Payload payload) {
        this.identifier = identifier;
        this.auth = auth;
        this.version = version;
        this.installLocation = installLocation;
        this.payload = payload;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstallLocation() {
        return installLocation;
    }

    public void setInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public BundleVersion getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(BundleVersion bundleVersion) {
        this.bundleVersion = bundleVersion;
    }
}
