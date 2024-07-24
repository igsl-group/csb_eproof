package hk.gov.ogcio.eproof.controller.pdf.types;

import com.lowagie.text.pdf.PdfWriter;

/**
 * Enum of PDF versions
 */
public enum PdfVersion {
    PDF_1_2("PDF-1.2", PdfWriter.VERSION_1_2), PDF_1_3("PDF-1.3", PdfWriter.VERSION_1_3), PDF_1_4("PDF-1.4",
            PdfWriter.VERSION_1_4), PDF_1_5("PDF-1.5", PdfWriter.VERSION_1_5), PDF_1_6("PDF-1.6",
            PdfWriter.VERSION_1_6), PDF_1_7("PDF-1.7", PdfWriter.VERSION_1_7);

    private final String name;
    private final char charVersion;

    private PdfVersion(final String aName, char aVersion) {
        name = aName;
        charVersion = aVersion;
    }

    /**
     * Gets version name.
     */
    public String getVersionName() {
        return name;
    }

    /**
     * Gets version as char (representation in PdfReader and PdfWriter).
     */
    public char getCharVersion() {
        return charVersion;
    }

    public static PdfVersion fromCharVersion(char ver) {
        for (PdfVersion pdfVer: PdfVersion.values()) {
            if (pdfVer.getCharVersion() == ver) {
                return pdfVer;
            }
        }
        return null;
    }
}

