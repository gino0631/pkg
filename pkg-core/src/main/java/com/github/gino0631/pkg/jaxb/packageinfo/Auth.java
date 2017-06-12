package com.github.gino0631.pkg.jaxb.packageinfo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Auth {
    @XmlEnumValue("none")
    NONE,

    @XmlEnumValue("root")
    ROOT
}
