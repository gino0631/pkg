package com.github.gino0631.pkg.jaxb.distribution;

import com.github.gino0631.pkg.jaxb.ObjectFactory;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Background {
    @XmlAttribute(name = "file", required = true)
    protected String file;

    @XmlAttribute(name = "alignment")
    protected Alignment alignment;

    @XmlAttribute(name = "scaling")
    protected Scaling scaling;

    @XmlEnum
    public enum Alignment {
        @XmlEnumValue("center")
        CENTER,

        @XmlEnumValue("left")
        LEFT,

        @XmlEnumValue("right")
        RIGHT,

        @XmlEnumValue("top")
        TOP,

        @XmlEnumValue("bottom")
        BOTTOM,

        @XmlEnumValue("topleft")
        TOP_LEFT,

        @XmlEnumValue("topright")
        TOP_RIGHT,

        @XmlEnumValue("bottomleft")
        BOTTOM_LEFT,

        @XmlEnumValue("bottomright")
        BOTTOM_RIGHT;
    }

    @XmlEnum
    public enum Scaling {
        @XmlEnumValue("tofit")
        TO_FIT,

        @XmlEnumValue("none")
        NONE,

        @XmlEnumValue("proportional")
        PROPORTIONAL;
    }

    public Background() {
    }

    public Background(String file, String alignment, String scaling) {
        this(file, ObjectFactory.getEnum(Alignment.class, alignment, true), ObjectFactory.getEnum(Scaling.class, scaling, true));
    }

    public Background(String file, Alignment alignment, Scaling scaling) {
        this.file = file;
        this.alignment = alignment;
        this.scaling = scaling;
    }
}
