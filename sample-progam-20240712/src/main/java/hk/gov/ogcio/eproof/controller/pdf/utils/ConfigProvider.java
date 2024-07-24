package hk.gov.ogcio.eproof.controller.pdf.utils;

import hk.gov.ogcio.eproof.controller.pdf.Constants;

/**
 * Property holder for tweak file.
 *
 * @author Josef Cacek
 */
public final class ConfigProvider extends PropertyProvider {

    protected ConfigProvider() {
    }

    /**
     * Returns instance of this class. (singleton)
     *
     * @return
     */
    public static ConfigProvider getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.jsignpdf.PropertyProvider#loadDefault()
     */
    @Override
    public void loadDefault() throws ProperyProviderException {
        loadProperties(IOUtils.findFile(Constants.CONF_FILE));
    }

    private static class InstanceHolder {
        static final ConfigProvider INSTANCE = new ConfigProvider();
    }
}

