package hk.gov.ogcio.eproof.controller.pdf.utils;

import org.apache.commons.lang3.StringUtils;


import java.io.File;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import static hk.gov.ogcio.eproof.controller.pdf.Constants.logger;


/**
 * Methods for handling PKCS#11 security providers.
 */
public final class PKCS11Utils {

    public static volatile Provider SUN_PROVIDER;
    public static volatile Provider JSIGN_PROVIDER;

    /**
     * Tries to register the sun.security.pkcs11.SunPKCS11 provider with configuration provided in the given file.
     *
     * @param configPath path to PKCS#11 provider configuration file
     * @return newly registered PKCS#11 provider name if provider successfully registered; <code>null</code> otherwise
     */
    public static void registerProviders(final String configPath) {
        if (StringUtils.isEmpty(configPath)) {
            return;
        }
        logger.debug("Registering SunPKCS11 provider from configuration in " + configPath);
        final File cfgFile = IOUtils.findFile(configPath);
        final String absolutePath = cfgFile.getAbsolutePath();
        if (cfgFile.isFile()) {
            SUN_PROVIDER = initPkcs11Provider(absolutePath, "sun.security.pkcs11.SunPKCS11");
            JSIGN_PROVIDER = initPkcs11Provider(absolutePath, "com.github.kwart.jsign.pkcs11.JSignPKCS11");
        } else {
            System.err.println("The PKCS#11 provider is not registered. Configuration file doesn't exist: " + absolutePath);
        }
    }

    /**
     * Unregisters PKCS11 security provider registered by {@link #registerProviders(String)} method.
     * <p>
     * Some tokens/card-readers hangs during second usage of the program, they have to be unplugged and plugged again following
     * code should prevent this issue.
     * </p>
     *
     */
    public static void unregisterProviders() {
        SUN_PROVIDER = unregisterProvider(SUN_PROVIDER);
        JSIGN_PROVIDER = unregisterProvider(JSIGN_PROVIDER);
        // we should wait a little bit to de-register provider correctly (is it a driver
        // issue?)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getProviderNameForKeystoreType(String type) {
        if (type == null) {
            return null;
        }
        String name = getProviderNameImpl(type, SUN_PROVIDER);
        if (name == null) {
            name = getProviderNameImpl(type, JSIGN_PROVIDER);
        }
        return name;
    }

    private static Provider initPkcs11Provider(String configPath, String className) {
        Provider pkcs11Provider = null;
        try {
            Class<?> sunPkcs11Cls = Class.forName(className);
            try {
                pkcs11Provider = (Provider) sunPkcs11Cls.getConstructor(String.class).newInstance(configPath);
            } catch (NoSuchMethodException e) {
                pkcs11Provider = (Provider) sunPkcs11Cls.getConstructor().newInstance();
                Class<Provider> provCls = Provider.class;
                pkcs11Provider = (Provider) provCls.getMethod("configure", String.class).invoke(pkcs11Provider, configPath);
            }
            Security.addProvider(pkcs11Provider);
            final String name = pkcs11Provider.getName();
            logger.debug("PKCS11 provider registered with name " + name);
        } catch (Throwable e) {
            logger.fatal("Unable to register SunPKCS11 security provider.", e);
        }
        return pkcs11Provider;
    }

    private static Provider unregisterProvider(Provider provider) {
        if (provider == null) {
            return null;
        }
        String providerName = provider.getName();
        logger.debug("Removing security provider with name " + providerName);
        try {
            Security.removeProvider(providerName);
        } catch (Exception e) {
            logger.fatal( "Removing provider failed", e);
        }
        return null;
    }

    private static String getProviderNameImpl(String type, Provider provider) {
        if (provider == null || type == null) {
            return null;
        }
        String providerName = provider.getName();
        try {
            KeyStore.getInstance(type, provider);
            logger.debug("KeyStore type " + type + " is supported by the provider " + providerName);
            return provider.getName();
        } catch (Exception e) {
            logger.debug("KeyStore type " + type + " is not supported by the provider " + providerName);
        }
        return null;
    }

}