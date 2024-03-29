package com.github.gino0631.pkg.jaxb.distribution;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Choice {
    @XmlAttribute(name = "id", required = true)
    protected String id;

    @XmlElement(name = "pkg-ref")
    protected List<PkgRef> pkgRefs;

    public Choice() {
    }

    public Choice(String id, List<PkgRef> pkgRefs) {
        this.id = id;
        this.pkgRefs = pkgRefs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PkgRef> getPkgRefs() {
        return pkgRefs;
    }

    public void setPkgRefs(List<PkgRef> pkgRefs) {
        this.pkgRefs = pkgRefs;
    }
}
