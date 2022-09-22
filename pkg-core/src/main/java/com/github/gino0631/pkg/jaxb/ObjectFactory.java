package com.github.gino0631.pkg.jaxb;

import com.github.gino0631.pkg.jaxb.distribution.InstallerScript;
import com.github.gino0631.pkg.jaxb.packageinfo.PkgInfo;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlEnumValue;
import java.io.OutputStream;
import java.lang.reflect.Field;

public final class ObjectFactory {
    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(PkgInfo.class, InstallerScript.class);

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectFactory() {
    }

    public static void marshal(Object jaxbObject, OutputStream os) {
        try {
            Marshaller jaxbMarshaller = JAXB_CONTEXT.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            jaxbMarshaller.marshal(jaxbObject, os);

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Enum<T>> T getEnum(Class<T> cls, String name, boolean ignoreCase) {
        if (name != null) {
            for (Field f : cls.getFields()) {
                if (f.isEnumConstant()) {
                    XmlEnumValue v = f.getAnnotation(XmlEnumValue.class);
                    if ((v != null) && (ignoreCase ? name.equalsIgnoreCase(v.value()) : name.equals(v.value()))) {
                        try {
                            //noinspection unchecked
                            return (T) f.get(null);

                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            throw new IllegalArgumentException("No enum constant for " + name);

        } else {
            return null;
        }
    }
}
