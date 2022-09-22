package com.github.gino0631.pkg.jaxb.distribution;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ChoicesOutline {
    @XmlElement(name = "line", required = true)
    protected List<Line> lines;

    public ChoicesOutline() {
    }

    public ChoicesOutline(List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }
}
