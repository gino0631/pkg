package com.github.gino0631.pkg.jaxb.distribution;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Options {
    @XmlAttribute(name = "customize")
    protected Customize customize;

    @XmlAttribute(name = "require-scripts")
    protected Boolean requireScripts;

    @XmlEnum
    public enum Customize {
        @XmlEnumValue("allow")
        ALLOW,

        @XmlEnumValue("always")
        ALWAYS,

        @XmlEnumValue("never")
        NEVER
    }

    public Options() {
    }

    public Options(Customize customize, Boolean requireScripts) {
        this.customize = customize;
        this.requireScripts = requireScripts;
    }

    public Customize getCustomize() {
        return customize;
    }

    public void setCustomize(Customize customize) {
        this.customize = customize;
    }

    public Boolean getRequireScripts() {
        return requireScripts;
    }

    public void setRequireScripts(Boolean requireScripts) {
        this.requireScripts = requireScripts;
    }
}
