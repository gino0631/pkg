package com.github.gino0631.pkg.maven;

import com.github.gino0631.pkg.core.ProductBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Signing configuration.
 * <p>
 * Most parameters are treated as in sun.security.tools.JarSigner.
 */
public class Signing {
    /**
     * Keystore path or URL.
     * The default is .keystore file in the user's home directory, as determined by the "user.home" system property.
     */
    @Parameter
    private String keystore;

    /**
     * Keystore type.
     * The default type is the one that is specified as the value of the "keystore.type" property in the security properties file,
     * which is returned by the {@link java.security.KeyStore#getDefaultType} method.
     */
    @Parameter
    private String storetype;

    /**
     * Keystore password.
     */
    @Parameter
    private String storepass;

    /**
     * Private key password.
     * If not specified, the same password as for the keystore will be used.
     */
    @Parameter
    private String keypass;

    /**
     * Keystore entry alias.
     */
    @Parameter
    private String alias;

    /**
     * Name of the provider to use.
     */
    @Parameter
    private String providerName;

    /**
     * Class name of the provider to instantiate and use.
     */
    @Parameter
    private String providerClass;

    /**
     * Argument to use when instantiating the provider (see {@link #providerClass}).
     */
    @Parameter
    private String providerArg;

    /**
     * URL of Time Stamping Authority (TSA) to use for signature time stamping.
     */
    @Parameter
    private URL tsa;

    /**
     * Flag to disable signing.
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    void configure(ProductBuilder pkgBuilder) throws MojoExecutionException {
        if (!skip) {
            Provider provider = getProvider();
            KeyStore ks = getKeyStore(provider);

            try {
                if (alias == null) {
                    List<String> aliases = Collections.list(ks.aliases());

                    if (aliases.size() == 1) {
                        alias = aliases.get(0);

                    } else {
                        throw new MojoExecutionException(MessageFormat.format("An alias must be specified as the keystore contains {0} aliases", aliases.size()));
                    }
                }

                PrivateKey privateKey = (PrivateKey) ks.getKey(alias, (keypass != null) ? getPass(keypass) : getPass(storepass));
                Certificate[] certificateChain = ks.getCertificateChain(alias);
                List<X509Certificate> certificates = Arrays.asList(Arrays.copyOf(certificateChain, certificateChain.length, X509Certificate[].class));

                pkgBuilder.setSigning(privateKey, certificates, provider, tsa);

            } catch (GeneralSecurityException e) {
                throw new MojoExecutionException("Error configuring signing", e);
            }
        }
    }

    private KeyStore getKeyStore(Provider provider) throws MojoExecutionException {
        if (storetype == null) {
            storetype = KeyStore.getDefaultType();
        }

        try {
            KeyStore ks = (provider != null) ? KeyStore.getInstance(storetype, provider) : KeyStore.getInstance(storetype);

            if ("NONE".equals(keystore)) {
                ks.load(null, getPass(storepass));

            } else {
                if (keystore == null) {
                    keystore = System.getProperty("user.home") + File.separator + ".keystore";
                }

                keystore = keystore.replace(File.separatorChar, '/');
                URL url = getUrl(keystore);

                try (InputStream is = url.openStream()) {
                    ks.load(is, getPass(storepass));
                }
            }

            return ks;

        } catch (GeneralSecurityException | IOException e) {
            throw new MojoExecutionException("Error configuring keystore", e);
        }
    }

    private Provider getProvider() throws MojoExecutionException {
        if (providerName != null) {
            if (providerClass != null) {
                throw new MojoExecutionException("Provider class can not be specified together with provider name");
            }

            if (providerArg != null) {
                throw new MojoExecutionException("Provider argument can not be specified together with provider name");
            }

            return Security.getProvider(providerName);

        } else if (providerClass != null) {
            try {
                Object providerObj;
                Class<?> provClass = Class.forName(providerClass);

                if (providerArg != null) {
                    Constructor<?> c = provClass.getConstructor(String.class);
                    providerObj = c.newInstance(providerArg);

                } else {
                    providerObj = provClass.newInstance();
                }

                if (!(providerObj instanceof Provider)) {
                    throw new MojoExecutionException(MessageFormat.format("Class {0} is not a provider class", providerClass));
                }

                return (Provider) providerObj;

            } catch (ReflectiveOperationException e) {
                throw new MojoExecutionException(MessageFormat.format("Error instantiating {0}", providerClass), e);
            }

        } else {
            return null;
        }
    }

    private static URL getUrl(String spec) throws MalformedURLException {
        try {
            return new URL(spec);

        } catch (MalformedURLException e) {
            // Try as file
            return new File(spec).toURI().toURL();
        }
    }

    private static char[] getPass(String pass) {
        return (pass != null) ? pass.toCharArray() : null;
    }
}
