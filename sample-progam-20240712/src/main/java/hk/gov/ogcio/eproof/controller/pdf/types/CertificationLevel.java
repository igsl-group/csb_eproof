package hk.gov.ogcio.eproof.controller.pdf.types;

import com.lowagie.text.pdf.PdfSignatureAppearance;

import static hk.gov.ogcio.eproof.controller.pdf.Constants.RES;

/**
 * Enum of possible certification levels used to Sign PDF.
 */
public enum CertificationLevel {

    NOT_CERTIFIED("certificationLevel.notCertified", PdfSignatureAppearance.NOT_CERTIFIED), CERTIFIED_NO_CHANGES_ALLOWED(
            "certificationLevel.noChanges",
            PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED), CERTIFIED_FORM_FILLING("certificationLevel.formFill",
            PdfSignatureAppearance.CERTIFIED_FORM_FILLING), CERTIFIED_FORM_FILLING_AND_ANNOTATIONS(
            "certificationLevel.formFillAnnot", PdfSignatureAppearance.CERTIFIED_FORM_FILLING_AND_ANNOTATIONS);

    private String msgKey;
    private int level;

    CertificationLevel(final String aMsgKey, final int aLevel) {
        msgKey = aMsgKey;
        level = aLevel;
    }

    /**
     * Returns internationalized description of a level.
     */
    public String toString() {
        return RES.get(msgKey);
    }

    /**
     * Returns Level as defined in iText.
     *
     * @return
     * @see PdfSignatureAppearance#setCertificationLevel(int)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns {@link CertificationLevel} instance for given code. If the code is not found,
     * {@link CertificationLevel#NOT_CERTIFIED} is returned.
     *
     * @param certLevelCode level code
     * @return not-null CertificationLevel instance
     */
    public static CertificationLevel findCertificationLevel(int certLevelCode) {
        for (CertificationLevel certLevel : values()) {
            if (certLevelCode == certLevel.getLevel()) {
                return certLevel;
            }
        }
        return NOT_CERTIFIED;
    }
}