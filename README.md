# PKG
[![Build Status](https://travis-ci.org/gino0631/pkg.svg?branch=master)](https://travis-ci.org/gino0631/pkg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gino0631/pkg-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.gino0631/pkg-maven-plugin)

A pure Java implementation of tools for building macOS installer packages.

# Requirements
* Maven 3
* Java 8

# Usage
The tools consist of a Java library and plugins for build systems (currently, only Maven is supported).

## Maven plugin
The first step is to add the plugin to your project:
```xml
<project>
  ...
  <build>
    <!-- To define the plugin version in your parent POM -->
    <pluginManagement>
        <plugin>
          <groupId>com.github.gino0631</groupId>
          <artifactId>pkg-maven-plugin</artifactId>
          <version>...</version>
        </plugin>
        ...
      </plugins>
    </pluginManagement>
    <!-- To use the plugin goals in your POM or parent POM -->
    <plugins>
      <plugin>
        <groupId>com.github.gino0631</groupId>
        <artifactId>pkg-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>create-macosx-pkg</id>
            <phase>package</phase>
            <goals><goal>product</goal></goals>
            <configuration>
              <root>${project.build.directory}/product/macosx-x64</root>
              ...
              <distribution>
                ...
              </distribution>
              <permissionSets>
                ...
              </permissionSets>
              <signing>
                ...
              </signing>
            </configuration>
          </execution>
        </executions>
      </plugin>
    ...
    </plugins>
    ...
  </build>
  ...
</project>
```

The most important configuration parameter is `root`, which specifies the payload to be installed. Its contents should be relative to the root directory on a target system.

The creation of the payload is out of the scope of this project, and should be performed using other means as appropriate. For example, if building a Java application installer, it might be convenient to start with a skeleton directory structure like this:
```sh
src
├── main
│   ├── macosx
│   │   └── ${product.name}.app
│   │       └── Contents
│   │           ├── Java
│   │           │   └── ${product.name}.cfg
│   │           ├── MacOS
│   │           │   ├── ${product.name}
│   │           │   └── libpackager.dylib
│   │           ├── Resources
│   │           └── Info.plist
```
and then turn it into the final structure by copying and generating the necessary components with `maven-resources-plugin`, `maven-dependency-plugin` and maybe some others:
```sh
target
├── product
│   ├── macosx-x64
│   │   └── Applications
│   │       └── My App.app
│   │           └── Contents
│   │               ├── Java
│   │               │   ├── My App.cfg
│   │               │   └── my-app.jar
│   │               ├── MacOS
│   │               │   ├── My App
│   │               │   └── libpackager.dylib
│   │               ├── PlugIns
│   │               │   └── Java.runtime
│   │               ├── Resources
│   │               └── Info.plist
```

The following parameters are required, but their default values will be constructed using your POM, so it is necessary to specify them only to change the defaults:
```xml
<outputFile>${project.artifactId}-${project.version}.pkg</outputFile>
<packageIdentifier>${project.groupId}.${project.artifactId}.pkg</packageIdentifier>
<packageVersion>${project.artifact.selectedVersion.majorVersion}.${project.artifact.selectedVersion.minorVersion}</packageVersion>
<packageTitle>${project.name}</packageTitle>
```

These optional parameters allow to control the installation experience:
```xml
<distribution>
  <background>
    ...
  </background>
  <license>
    ...
  </license>
  <readme>...</readme>
  <conclusion>...</conclusion>
</distribution>
```

Background image for the installer can be specified like this:
```xml
<background>
  <alignment>bottomLeft</alignment>
  <resources>
    <resource>${basedir}/src/setup/macosx/background.png</resource>
  </resources>
</background>
```

The license to be displayed can be specified using either a simple:
```xml
<license>${basedir}/src/setup/license.rtf</license>
```
or a complete form, if multiple languages have to be supported:
```xml
<license>
  <resources>
    <resource>
      <file>${basedir}/src/setup/license-en.rtf</file>
      <lang>en</lang>
    </resource>
    <resource>
      <file>${basedir}/src/setup/license-de.rtf</file>
      <lang>de</lang>
    </resource>
  </resources>
</license>
```

Similarly, readme and conclusion texts can be specified.

For more details, refer to the [Distribution Definition XML Schema Reference](https://developer.apple.com/library/content/documentation/DeveloperTools/Reference/DistributionDefinitionRef/Chapters/Introduction.html).

By default, all installed files and directories will have `0644` and `0755` permissions set accordingly. To change this, use `permissionSets`, for example:
```xml
<permissionSets>
  <permissionSet>
    <includes>
      <include>**/MacOS/My App</include>
    </includes>
    <fileMode>0755</fileMode>
  </permissionSet>
</permissionSets>
```
The `permissionSet`s are processed in the order they are specified, and every `permissionSet` that matches (according to its `include` and `exclude` patterns) sets `fileMode`, `directoryMode`, `uid` and `gid` (if they are specified) on the file or directory in question.

Finally, to prevent Gatekeeper from showing warnings, the package must be signed using your Developer ID Installer certificate. This can be accomplished by specifying the relevant `signing` parameters:
```xml
<signing>
  <!-- Values defined in project properties and overridable via %M2_HOME%/settings.xml -->
  <keystore>${signing.macosx.installer.keystore}</keystore>
  <storetype>${signing.macosx.installer.storetype}</storetype>
  <storepass>${signing.macosx.installer.storepass}</storepass>
  <tsa>http://timestamp.apple.com/ts01</tsa>
  <skip>${signing.macosx.installer.skip}</skip>
</signing>
```

## Standalone library
Add a dependency on `com.github.gino0631:pkg-core` to your project, and use `ProductBuilder` class to build product packages.

# Credits
The project makes use of [jbomutils](https://github.com/jindrapetrik/jbomutils) by Jindra Petřík, and is inspired by [this tutorial](http://bomutils.dyndns.org/tutorial.html) by Fabian Renn.
