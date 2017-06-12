package com.github.gino0631.pkg.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

public class Multilingual {
    @Parameter
    private List<Resource> resources;

    public void set(String file) {
        resources = Collections.singletonList(new Resource().set(file));
    }

    public Map<String, Path> getFiles() throws MojoExecutionException {
        Map<String, Path> result = new HashMap<>(resources.size());

        for (Resource r : resources) {
            if (result.put(r.getLang(), Paths.get(r.getFile())) != null) {
                throw new MojoExecutionException(MessageFormat.format("Duplicate resource {0} for the same language", r.getFile()));
            }
        }

        return result;
    }

    public String getFileName(String defaultName) {
        if (resources.size() == 1) {
            return Paths.get(resources.get(0).getFile()).getFileName().toString();

        } else {
            return appendCommonExtension(defaultName);
        }
    }

    private String appendCommonExtension(String name) {
        String ext = getCommonExtension();

        if (ext != null) {
            name = name + '.' + ext;
        }

        return name;
    }

    private String getCommonExtension() {
        Set<String> extensions = new HashSet<>();

        for (Resource r : resources) {
            String fileName = Paths.get(r.getFile()).getFileName().toString();
            extensions.add(getExtension(fileName));
        }

        return (extensions.size() == 1) ? extensions.iterator().next() : null;
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        return (dotIndex == -1) ? null : fileName.substring(dotIndex + 1);
    }
}
