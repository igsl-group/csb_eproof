package hk.gov.ogcio.eproof.controller.pdf;

import hk.gov.ogcio.eproof.controller.pdf.ssl.SSLInitializer;
import hk.gov.ogcio.eproof.controller.pdf.types.CertificationLevel;
import hk.gov.ogcio.eproof.controller.pdf.types.HashAlgorithm;
import hk.gov.ogcio.eproof.controller.pdf.types.PDFEncryption;
import hk.gov.ogcio.eproof.controller.pdf.types.PrintRight;

import static hk.gov.ogcio.eproof.controller.pdf.Constants.*;

public final class Signer {


    /**
     * Main.
     *
     * @param args
     */
    public static void main(String[] args) {
        BasicSignerOptions options = new BasicSignerOptions();
        ;

        try {
            SSLInitializer.init();
        } catch (Exception e) {
            logger.warn("Unable to re-configure SSL layer", e);
        }

        options.setKsType("PKCS12");
        options.setAdvanced(true);
        options.setKsFile("C:\\Users\\user\\Desktop\\OGCIO\\cert\\server.p12");
        options.setKsPasswd("abc123");
        options.setStorePasswords(false);
        options.setInFile("C:\\Users\\user\\Desktop\\OGCIO\\pdf-utilities\\src\\test\\java\\resources\\HK_Registered_Gas_Installer_Card_Desktop_Demo_v1.pdf");
        options.setPdfEncryption(PDFEncryption.PASSWORD);
        options.setPdfOwnerPwd("abc123");
        options.setPdfUserPwd("");
        options.setPdfEncryptionCertFile("");
        options.setOutFile("C:\\Users\\user\\Desktop\\OGCIO\\pdf-utilities\\src\\test\\java\\resources\\HK_Registered_Gas_Installer_Card_Desktop_Demo_v1_signed.pdf");
        options.setCertLevel(CertificationLevel.CERTIFIED_NO_CHANGES_ALLOWED);
        options.setHashAlgorithm(HashAlgorithm.SHA256);
        options.setAppend(false);

        options.setRightPrinting(PrintRight.ALLOW_PRINTING);
        options.setRightCopy(false);
        options.setRightAssembly(false);
        options.setRightFillIn(false);
        options.setRightScreenReaders(false);
        options.setRightModifyAnnotations(false);
        options.setRightModifyContents(false);

        options.setVisible(false);

        SignerLogic signerLogic = new SignerLogic(options);
        boolean success = signerLogic.signFile();
    }


}
