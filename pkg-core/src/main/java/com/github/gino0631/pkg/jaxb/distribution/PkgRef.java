package com.github.gino0631.pkg.jaxb.distribution;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class PkgRef {
    @XmlAttribute(name = "id", required = true)
    protected String id;

    @XmlAttribute(name = "version")
    protected String version;

    @XmlAttribute(name = "installKBytes")
    protected Integer installKBytes;

    @XmlValue
    protected String content;

    public PkgRef() {
    }

    public PkgRef(String id) {
        this.id = id;
    }

    public PkgRef(String id, String version, Integer installKBytes, String content) {
        this.id = id;
        this.version = version;
        this.installKBytes = installKBytes;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getInstallKBytes() {
        return installKBytes;
    }

    public void setInstallKBytes(Integer installKBytes) {
        this.installKBytes = installKBytes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
