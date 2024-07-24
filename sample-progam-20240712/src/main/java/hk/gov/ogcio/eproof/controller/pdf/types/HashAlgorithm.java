package hk.gov.ogcio.eproof.controller.pdf.types;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Enum of hash algorithms supported in PDF signatures.
 */
public enum HashAlgorithm {
    SHA1("SHA-1", PdfVersion.PDF_1_3), SHA256("SHA-256", PdfVersion.PDF_1_6), SHA384("SHA-384", PdfVersion.PDF_1_7), SHA512("SHA-512",
            PdfVersion.PDF_1_7), RIPEMD160("RIPEMD160", PdfVersion.PDF_1_7);

    private final PdfVersion pdfVersion;
    private final String algorithmName;

    private HashAlgorithm(final String aName, PdfVersion aVersion) {
        algorithmName = aName;
        pdfVersion = aVersion;
    }

    /**
     * Gets algorithm name.
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Gets minimal PDF version supporting the algorithm.
     */
    public PdfVersion getPdfVersion() {
        return pdfVersion;
    }

    public String toStringWithPdfVersion() {
        return algorithmName + " (" + pdfVersion.getVersionName() + ")";
    }

    public static String valuesWithPdfVersionAsString() {
        return Stream.of(values()).map(ha -> ha.toStringWithPdfVersion()).collect(joining(", "));
    }
}