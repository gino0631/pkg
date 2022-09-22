package com.github.gino0631.pkg.jaxb.packageinfo;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Auth {
    @XmlEnumValue("none")
    NONE,

    @XmlEnumValue("root")
    ROOT
}
