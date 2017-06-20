package com.github.gino0631.pkg.jaxb.distribution;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Conclusion {
    @XmlAttribute(name = "file", required = true)
    protected String file;

    public Conclusion() {
    }

    public Conclusion(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
