package hk.gov.ogcio.eproof.controller.pdf.utils;

import java.io.File;

import static hk.gov.ogcio.eproof.controller.pdf.Constants.ENV_JSIGNPDF_HOME;
import static hk.gov.ogcio.eproof.controller.pdf.Constants.SYSPROP_JSIGNPDF_HOME;

/**
 * IO and file utils.
 *
 * @author Josef Cacek
 */
public final class IOUtils {

    /**
     * Finds given filepath within JSignPdf home - checking the alternative locations first.
     *
     * @see Constants#SYSPROP_JSIGNPDF_HOME
     * @see Constants#ENV_JSIGNPDF_HOME
     */
    public static File findFile(String filePath) {
        File file = getIfExists(filePath, SYSPROP_JSIGNPDF_HOME);
        if (file == null) {
            file = getIfExists(filePath, ENV_JSIGNPDF_HOME);
            if (file == null) {
                file = new File(filePath);
            }
        }
        return file;
    }

    private static File getIfExists(String filePath, String jsignpdfHomeDir) {
        if (jsignpdfHomeDir == null) {
            return null;
        }
        File file = new File(jsignpdfHomeDir, filePath);
        return file.isFile() && file.canRead() ? file : null;
    }
}

