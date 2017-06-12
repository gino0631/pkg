package com.github.gino0631.pkg.jaxb.packageinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Payload {
    @XmlAttribute(name = "installKBytes", required = true)
    protected Integer installKBytes;

    @XmlAttribute(name = "numberOfFiles", required = true)
    protected Integer numberOfFiles;

    public Payload() {
    }

    public Payload(Integer installKBytes, Integer numberOfFiles) {
        this.installKBytes = installKBytes;
        this.numberOfFiles = numberOfFiles;
    }

    public Integer getInstallKBytes() {
        return installKBytes;
    }

    public void setInstallKBytes(Integer installKBytes) {
        this.installKBytes = installKBytes;
    }

    public Integer getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }
}
