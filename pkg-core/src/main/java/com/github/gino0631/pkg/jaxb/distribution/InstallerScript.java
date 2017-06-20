package com.github.gino0631.pkg.jaxb.distribution;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "installer-script")
public class InstallerScript {
    @XmlAttribute(name = "minSpecVersion", required = true)
    protected String minSpecVersion = "1";

    @XmlElement(name = "title", required = true)
    protected String title;

    @XmlElement(name = "options")
    protected Options options;

    @XmlElement(name = "background")
    protected Background background;

    @XmlElement(name = "license")
    protected License license;

    @XmlElement(name = "readme")
    protected Readme readme;

    @XmlElement(name = "conclusion")
    protected Conclusion conclusion;

    @XmlElement(name = "choices-outline", required = true)
    protected ChoicesOutline choicesOutline;

    @XmlElement(name = "choice", required = true)
    protected List<Choice> choices;

    @XmlElement(name = "pkg-ref", required = true)
    protected List<PkgRef> pkgRefs;

    public InstallerScript() {
    }

    public InstallerScript(String title, Options options, ChoicesOutline choicesOutline, List<Choice> choices, List<PkgRef> pkgRefs) {
        this.title = title;
        this.options = options;
        this.choicesOutline = choicesOutline;
        this.choices = choices;
        this.pkgRefs = pkgRefs;
    }

    public String getMinSpecVersion() {
        return minSpecVersion;
    }

    public void setMinSpecVersion(String minSpecVersion) {
        this.minSpecVersion = minSpecVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Readme getReadme() {
        return readme;
    }

    public void setReadme(Readme readme) {
        this.readme = readme;
    }

    public Conclusion getConclusion() {
        return conclusion;
    }

    public void setConclusion(Conclusion conclusion) {
        this.conclusion = conclusion;
    }

    public ChoicesOutline getChoicesOutline() {
        return choicesOutline;
    }

    public void setChoicesOutline(ChoicesOutline choicesOutline) {
        this.choicesOutline = choicesOutline;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public List<PkgRef> getPkgRefs() {
        return pkgRefs;
    }

    public void setPkgRefs(List<PkgRef> pkgRefs) {
        this.pkgRefs = pkgRefs;
    }
}
