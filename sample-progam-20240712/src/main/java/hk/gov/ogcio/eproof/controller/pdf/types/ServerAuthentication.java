package hk.gov.ogcio.eproof.controller.pdf.types;


import static hk.gov.ogcio.eproof.controller.pdf.Constants.RES;

/**
 * A server authentication methods enum.
 */
public enum ServerAuthentication {
    NONE("serverAuthn.none"), PASSWORD("serverAuthn.password"), CERTIFICATE("serverAuthn.certificate");

    private String msgKey;

    ServerAuthentication(final String aMsgKey) {
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