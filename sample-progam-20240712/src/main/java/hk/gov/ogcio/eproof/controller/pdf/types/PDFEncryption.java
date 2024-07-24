package hk.gov.ogcio.eproof.controller.pdf.types;

import static hk.gov.ogcio.eproof.controller.pdf.Constants.RES;

/**
 * PDF encryption type.
 */
public enum PDFEncryption {
    NONE("pdfEncryption.notEncrypted"), PASSWORD("pdfEncryption.password"), CERTIFICATE("pdfEncryption.certificate");

    private String msgKey;

    PDFEncryption(final String aMsgKey) {
        msgKey = aMsgKey;
    }

    /**
     * Returns internationalized description of a level.
     */
    @Override
    public String toString() {
        return RES.get(msgKey);
    }

}