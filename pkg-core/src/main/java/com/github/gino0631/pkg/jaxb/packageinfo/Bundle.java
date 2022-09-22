package com.github.gino0631.pkg.jaxb.packageinfo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Bundle {
    @XmlAttribute(name = "path")
    protected String path;

    @XmlAttribute(name = "id")
    protected String id;

    @XmlAttribute(name = "CFBundleIdentifier")
    protected String cfBundleIdentifier;

    @XmlAttribute(name = "CFBundleVersion")
    protected String cfBundleVersion;

    public Bundle() {
    }

    public Bundle(String path, String id, String cfBundleIdentifier, String cfBundleVersion) {
        this.path = path;
        this.id = id;
        this.cfBundleIdentifier = cfBundleIdentifier;
        this.cfBundleVersion = cfBundleVersion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCfBundleIdentifier() {
        return cfBundleIdentifier;
    }

    public void setCfBundleIdentifier(String cfBundleIdentifier) {
        this.cfBundleIdentifier = cfBundleIdentifier;
    }

    public String getCfBundleVersion() {
        return cfBundleVersion;
    }

    public void setCfBundleVersion(String cfBundleVersion) {
        this.cfBundleVersion = cfBundleVersion;
    }
}
