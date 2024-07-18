package hk.gov.fsd.eis.esign.Util;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.*;
import org.bouncycastle.cert.jcajce.*;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.*;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.*;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

public class pdfBoxSign extends CreateSignatureBase {
	/**
	 * Initialize the signature creator with a keystore (pkcs12) and pin that should be used for the
	 * signature.
	 *
	 * @param keystore is a pkcs12 keystore.
	 * @param pin      is the pin for the keystore / private key
	 * @throws KeyStoreException         if the keystore has not been initialized (loaded)
	 * @throws NoSuchAlgorithmException  if the algorithm for recovering the key cannot be found
	 * @throws UnrecoverableKeyException if the given password is wrong
	 * @throws CertificateException      if the certificate is not valid as signing time
	 * @throws IOException               if no certificate could be found
	 */
	public pdfBoxSign(KeyStore keystore, char[] pin) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {
		super(keystore, pin);
	}

	public void signDetached(PDDocument document, OutputStream output, String reason, String location)
			throws IOException
	{
		// call SigUtils.checkCrossReferenceTable(document) if Adobe complains
		// and read https://stackoverflow.com/a/71293901/535646
		// and https://issues.apache.org/jira/browse/PDFBOX-5382


		// add metadata [keyword] to pdf
//		PDDocumentInformation pdd = document.getDocumentInformation();
//		pdd.setTitle("Signed by Apache PDFBox!");
//		pdd.setKeywords("eyJ1dWlkIjoiYmQ2NDIxOTAtMzczMS00M2U2LTgzNTItM2RhYjdkOTYwMWZjIiwidmVyc2lvbiI6MSwidmVyaWZpY2F0aW9uTWV0aG9kSWQiOiJkaWQ6ZXByb29mOmJkN2UzMWFhLTI2YjItNGQ4MS05NzRjLWFlN2IxMmY0ZGE3YSNwMTEifQ==");
//		System.out.println("Setting Keywords metadata: " + pdd.getKeywords());
		// document.setDocumentInformation(pdd);

		int accessPermissions = SigUtils.getMDPPermission(document);
		if (accessPermissions == 1)
		{
			throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
		}

		// create signature dictionary
		PDSignature signature = new PDSignature();
		signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
		signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
//		signature.setName("Example User");
		signature.setLocation(location);
		signature.setReason(reason);
		// TODO extract the above details from the signing certificate? Reason as a parameter?

		// the signing date, needed for valid signature
		signature.setSignDate(Calendar.getInstance());

		// Optional: certify
		if (accessPermissions == 0)
		{
			SigUtils.setMDPPermission(document, signature, 2);
		}

		if (isExternalSigning())
		{
			document.addSignature(signature);
			ExternalSigningSupport externalSigning =
					document.saveIncrementalForExternalSigning(output);
			// invoke external signature service
			byte[] cmsSignature = sign(externalSigning.getContent());
			// set signature bytes received from the service
			externalSigning.setSignature(cmsSignature);
		}
		else
		{
			SignatureOptions signatureOptions = new SignatureOptions();
			// Size can vary, but should be enough for purpose.
			signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);
			// register signature dictionary and sign interface
			document.addSignature(signature, this, signatureOptions);

			// write incremental (only for signing purpose)
			document.saveIncremental(output);
		}
	}
}
