package hk.gov.ogcio.eproof.controller.pdf.types;

import com.lowagie.text.pdf.PdfSignatureAppearance;
import static hk.gov.ogcio.eproof.controller.pdf.Constants.RES;

/**
 * Enum for visible sign rendering configuration
 */
public enum RenderMode {

    DESCRIPTION_ONLY("render.descriptionOnly", PdfSignatureAppearance.SignatureRenderDescription), GRAPHIC_AND_DESCRIPTION(
            "render.graphicAndDescription",
            PdfSignatureAppearance.SignatureRenderGraphicAndDescription), SIGNAME_AND_DESCRIPTION(
            "render.signameAndDescription", PdfSignatureAppearance.SignatureRenderNameAndDescription);

    private String msgKey;
    private int render;

    RenderMode(final String aMsgKey, final int aLevel) {
        msgKey = aMsgKey;
        render = aLevel;
    }

    /**
     * Returns internationalized description of a right.
     */
    @Override
    public String toString() {
        return RES.get(msgKey);
    }

    /**
     * Returns Visible Signature Render flag.
     *
     * @return integer flag
     * @see PdfSignatureAppearance#setRender(int)
     */
    public int getRender() {
        return render;
    }

}
