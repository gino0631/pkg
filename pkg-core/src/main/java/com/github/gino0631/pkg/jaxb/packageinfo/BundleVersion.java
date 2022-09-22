package com.github.gino0631.pkg.jaxb.packageinfo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class BundleVersion {
    @XmlElement(name = "bundle")
    protected List<Bundle> bundles;

    public BundleVersion() {
    }

    public BundleVersion(List<Bundle> bundles) {
        this.bundles = bundles;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }

    public void setBundles(List<Bundle> bundles) {
        this.bundles = bundles;
    }
}
