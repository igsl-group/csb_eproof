package hk.gov.ogcio.eproof.controller.pdf;

/**
 * Listener class for "signing finished" event.
 */
public interface SignResultListener {

    /**
     * Method fired when signer finishes. Parameter says if it was successful.
     *
     * @param e null if finished succesfully or the reason (Exception)
     */
    void signerFinishedEvent(Throwable e);
}

