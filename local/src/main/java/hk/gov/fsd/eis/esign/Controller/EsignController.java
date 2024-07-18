package hk.gov.fsd.eis.esign.Controller;
import com.google.common.base.*;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import hk.gov.fsd.eis.esign.Util.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.graphics.image.*;
import org.apache.xmpbox.*;
import org.apache.xmpbox.schema.*;
import org.apache.xmpbox.xml.*;
import org.bouncycastle.asn1.x500.*;
import org.bouncycastle.asn1.x500.style.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.jcajce.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;
import java.util.List;


@RestController
public class EsignController{
	@Value("${path.config0}")
	private String configSlot0Name;
	@Value("${path.config1}")
	private String configSlot1Name;
	@Value("${path.dest}")
	private String dest;
	@Value("${simulation}")
	private boolean simulation;
	@Value("${error.init.not.found}")
	private String errorMessageNotFound;
	@Value("${error.init.too.many}")
	private String errorMessageTooMany;
	@Value("${error.init.default}")
	private String errorMessageDefault;
	@Value("${error.public.key.not.match}")
	private String errorMessageNotMatch;
	@Value("${simulation.path.cert}")
	private String simulationCertPath;
	@Value("${simulation.path.unsigned.pdf}")
	private String simulationUnsignedPdfPath;
	@Value("${simulation.path.unsigned.string}")
	private String simulationUnsignedStringPath;
	@Value("${simulation.path.signed.string}")
	private String simulationSignedStringPath;
	@Value("${simulation.silence}")
	private boolean simulationSilence;

	private Provider providerPKCS11;
	private PrivateKey pk;
	private Certificate outputPublicKey;
	private KeyStore ks;
	private HttpStatus initResponseCode;
	private String commonName;
	private String pin ="";

	private static final Logger logger = LoggerFactory.getLogger(EsignController.class);

	public boolean init() {
		try{
			initResponseCode = HttpStatus.OK;

			Provider providerPKCS11Slot0 = Security.getProvider("SunPKCS11");
			boolean hasSlot0 = true;
			KeyStore ksSlot0 = null;
			String aliasSlot0 = null;
			List pqListSlot0 = null;

			providerPKCS11Slot0 = providerPKCS11Slot0.configure(configSlot0Name);
			Security.addProvider(providerPKCS11Slot0);

			try{
				ksSlot0 = KeyStore.getInstance("PKCS11", providerPKCS11Slot0);
				ksSlot0.load(null, pin.toCharArray());

				if(hasSlot0){
					aliasSlot0 = (String)ksSlot0.aliases().nextElement();
					pqListSlot0 = new ArrayList();
					byte[] policyBytes = ((X509Certificate) ksSlot0.getCertificate(aliasSlot0)).getExtensionValue("2.5.29.32");
					if (policyBytes != null) {
						CertificatePolicies policies = CertificatePolicies.getInstance(JcaX509ExtensionUtils.parseExtensionValue(policyBytes));
						PolicyInformation[] policyInformation = policies.getPolicyInformation();
						for (PolicyInformation pInfo : policyInformation) {
							pqListSlot0.add(pInfo.getPolicyIdentifier().getId());
						}
					}
				}
			}catch (Exception e){
				logger.warn("Caught exception", e);
				hasSlot0 = false;
			}

			Provider providerPKCS11Slot1 = Security.getProvider("SunPKCS11");
			boolean hasSlot1 = true;
			KeyStore ksSlot1 = null;
			String aliasSlot1 = null;
			List pqListSlot1 = null;

			providerPKCS11Slot1 = providerPKCS11Slot1.configure(configSlot1Name);
			Security.addProvider(providerPKCS11Slot1);

			try{
				ksSlot1 = KeyStore.getInstance("PKCS11", providerPKCS11Slot1);
				ksSlot1.load(null, pin.toCharArray());
				aliasSlot1 = (String)ksSlot1.aliases().nextElement();

				pqListSlot1 = new ArrayList();
				byte[] policyBytes = ((X509Certificate) ksSlot1.getCertificate(aliasSlot1)).getExtensionValue("2.5.29.32");
				if (policyBytes != null) {
					CertificatePolicies policies = CertificatePolicies.getInstance(JcaX509ExtensionUtils.parseExtensionValue(policyBytes));
					PolicyInformation[] policyInformation = policies.getPolicyInformation();
					for (PolicyInformation pInfo : policyInformation) {
						pqListSlot1.add(pInfo.getPolicyIdentifier().getId());
					}
				}
			}catch (Exception e){
				logger.warn("Caught exception", e);
				hasSlot1 = false;
			}

			if(hasSlot0 && !hasSlot1){
				if(pqListSlot0.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1){
					this.initResponseCode = HttpStatus.BAD_REQUEST;
					return false;
				} else{
					providerPKCS11 = providerPKCS11Slot0;
					ks = ksSlot0;
					pk = (PrivateKey)ks.getKey(aliasSlot0, pin.toCharArray());
					outputPublicKey = ks.getCertificate(aliasSlot0);
				}
			}

			if(!hasSlot0 && hasSlot1){
				if(pqListSlot1.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1){
					this.initResponseCode = HttpStatus.NOT_FOUND;
					return false;
				} else{
					providerPKCS11 = providerPKCS11Slot1;
					ks = ksSlot1;
					pk = (PrivateKey)ks.getKey(aliasSlot1, pin.toCharArray());
					outputPublicKey = ks.getCertificate(aliasSlot1);
				}
			}

			if(hasSlot0 & hasSlot1){
				if(pqListSlot0.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1  && pqListSlot1.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1){
					this.initResponseCode = HttpStatus.BAD_REQUEST;
					return false;
				}

				if(pqListSlot0.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") != -1  && pqListSlot1.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") != -1){
					this.initResponseCode = HttpStatus.BAD_REQUEST;
					return false;
				}

				if(pqListSlot0.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") != -1  && pqListSlot1.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1){
					providerPKCS11 = providerPKCS11Slot0;
					ks = ksSlot0;
					pk = (PrivateKey)ks.getKey(aliasSlot0, pin.toCharArray());
					outputPublicKey = ks.getCertificate(aliasSlot0);
				}

				if(pqListSlot0.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1  && pqListSlot1.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") != -1){
					providerPKCS11 = providerPKCS11Slot1;
					ks = ksSlot1;
					pk = (PrivateKey)ks.getKey(aliasSlot1, pin.toCharArray());
					outputPublicKey = ks.getCertificate(aliasSlot1);
				}
			}

			JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) outputPublicKey);
			X500Name x500name = certHolder.getSubject();
			RDN cn = x500name.getRDNs(BCStyle.CN)[0];
			commonName = IETFUtils.valueToString(cn.getFirst().getValue());

			return true;
		}catch(NullPointerException e){
			logger.warn("Caught exception", e);
			this.initResponseCode = HttpStatus.NOT_FOUND;
			return false;
		}catch(Exception e){
			logger.warn("Caught exception", e);
			this.initResponseCode = HttpStatus.INTERNAL_SERVER_ERROR;
			return false;
		}
	}

//	public boolean init() {
//		try{
//			initErrorMessage = null;
//
//			providerPKCS11 = Security.getProvider("SunPKCS11");
//			providerPKCS11 = providerPKCS11.configure(configName);
//			Security.addProvider(providerPKCS11);
//
//			ks = KeyStore.getInstance("PKCS11", providerPKCS11);
//			ks.load(null, pin.toCharArray());
//
//			String alias = (String)ks.aliases().nextElement();
//
//			Certificate[] chain = ks.getCertificateChain(alias);
//			pk = (PrivateKey)ks.getKey(alias, pin.toCharArray());
//			outputPublicKey = ks.getCertificate(alias);
//
//			JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) outputPublicKey);
//			X500Name x500name = certHolder.getSubject();
//			RDN cn = x500name.getRDNs(BCStyle.CN)[0];
//			commonName = IETFUtils.valueToString(cn.getFirst().getValue());
//
//			List pqList = new ArrayList();
//			byte[] policyBytes = ((X509Certificate) outputPublicKey).getExtensionValue("2.5.29.32");
//			if (policyBytes != null) {
//				CertificatePolicies policies = CertificatePolicies.getInstance(JcaX509ExtensionUtils.parseExtensionValue(policyBytes));
//				PolicyInformation[] policyInformation = policies.getPolicyInformation();
//				for (PolicyInformation pInfo : policyInformation) {
//					pqList.add(pInfo.getPolicyIdentifier().getId());
//				}
//			}
//
//			if(pqList.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1){
//				this.initErrorMessage = "Certificate policy identifier not match.";
//				return false;
//			}
//
//		}catch(Exception e){
//			logger.warn("Caught exception", e);
//			this.initErrorMessage = "Cannot identify the token.";
//			return false;
//		}
//
//		return true;
//	}

//	public boolean init(int slot) throws Exception {
//		try{
//			initErrorMessage = null;
//			System.out.println("config" + slot + ".txt");
//			providerPKCS11 = Security.getProvider("SunPKCS11");
//			providerPKCS11 = providerPKCS11.configure("config" + slot + ".txt");
//			Security.addProvider(providerPKCS11);
//
//			System.out.println("init: " + providerPKCS11.getName());
//
//			ks = KeyStore.getInstance("PKCS11", providerPKCS11);
//			//String pin = "57998156";
//			ks.load(null, pin.toCharArray());
//
//			String alias = (String)ks.aliases().nextElement();
//
//			Certificate[] chain = ks.getCertificateChain(alias);
//			pk = (PrivateKey)ks.getKey(alias, pin.toCharArray());
//			outputPublicKey = ks.getCertificate(alias);
//
//			JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) outputPublicKey);
//			X500Name x500name = certHolder.getSubject();
//			RDN cn = x500name.getRDNs(BCStyle.CN)[0];
//			commonName = IETFUtils.valueToString(cn.getFirst().getValue());
//		}catch(Exception e){
//			logger.warn("Caught exception", e);
//			this.initErrorMessage = "Cannot identify the token.";
//			return false;
//		}
//
//		List pqList = new ArrayList();
//		byte[] policyBytes = ((X509Certificate) outputPublicKey).getExtensionValue("2.5.29.32");
//		if (policyBytes != null) {
//			CertificatePolicies policies = CertificatePolicies.getInstance(JcaX509ExtensionUtils.parseExtensionValue(policyBytes));
//			PolicyInformation[] policyInformation = policies.getPolicyInformation();
//			for (PolicyInformation pInfo : policyInformation) {
//				pqList.add(pInfo.getPolicyIdentifier().getId());
//			}
//		}
//
//		if(pqList.indexOf("2.16.344.8.2.2008.810.2.2018.1.1") == -1){
//			this.initErrorMessage = "Certificate policy identifier not match.";
//			System.out.println(initErrorMessage);
//			return false;
//		}
//
//		return true;
//	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "signingCert", method = RequestMethod.GET)
	public ResponseEntity signingCert(@RequestParam(name="debug", required=false) boolean debug) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
		if (simulation) {
			Path filePath = Path.of(simulationCertPath);
			try {
				String fileContent = Files.readString(filePath, StandardCharsets.UTF_8);
				logger.debug("File content:\n" + fileContent);
				return ResponseEntity.ok(fileContent);
			} catch (Exception e) {
				logger.debug("Error reading file: " + e.getMessage());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource("Error".getBytes()));
			}
		}

		if(providerPKCS11 != null){
			Security.removeProvider(providerPKCS11.getName());
			providerPKCS11 = null;
			ks = null;
			pk = null;
			outputPublicKey = null;
			commonName = null;
		}

		if(!init()){
			if(initResponseCode == HttpStatus.BAD_REQUEST)
				return ResponseEntity.status(initResponseCode).body(new ByteArrayResource(errorMessageTooMany.getBytes()));
			if(initResponseCode == HttpStatus.INTERNAL_SERVER_ERROR)
				return ResponseEntity.status(initResponseCode).body(new ByteArrayResource(errorMessageDefault.getBytes()));
			if(initResponseCode == HttpStatus.NOT_FOUND)
				return ResponseEntity.status(initResponseCode).body(new ByteArrayResource(errorMessageNotFound.getBytes()));
		}

		String publicKeyCert = Base64.getEncoder().encodeToString(outputPublicKey.getEncoded());
		String publicKeyFormatted = "-----BEGIN PUBLIC KEY-----" + "\r\n";
		for (final String row: Splitter.fixedLength(64).split(publicKeyCert))
		{
			publicKeyFormatted += row + "\r\n";
		}
		publicKeyFormatted += "-----END PUBLIC KEY-----";

		String out = Base64.getEncoder().encodeToString(publicKeyFormatted.getBytes());
		if(debug)
			out += "\nCN: " + commonName;

		return ResponseEntity.ok(out);
	}

	private boolean comparePublicKeyCert(String cert) throws CertificateEncodingException {
		String publicKeyCert = Base64.getEncoder().encodeToString(outputPublicKey.getEncoded());
		String publicKeyFormatted = "-----BEGIN PUBLIC KEY-----" + "\r\n";
		for (final String row: Splitter.fixedLength(64).split(publicKeyCert))
		{
			publicKeyFormatted += row + "\r\n";
		}
		publicKeyFormatted += "-----END PUBLIC KEY-----";

		logger.debug("comparePublicKeyCert: " + Base64.getEncoder().encodeToString(publicKeyFormatted.getBytes()));
		return cert.equals(Base64.getEncoder().encodeToString(publicKeyFormatted.getBytes()));
	}

//	@CrossOrigin(origins = "*")
//	@RequestMapping(value = "getUserCertInfo", method = RequestMethod.GET)
//	public ResponseEntity getUserCertInfo() throws Exception {
//		String out = "";
//		boolean eisTokenAlreadyFound = false;
//		for(int i=0; i<4;i++){
//			if(providerPKCS11 != null){
//				System.out.println(i + " : " + providerPKCS11.getName());
//				System.out.println(providerPKCS11.getName());
//				Security.removeProvider(providerPKCS11.getName());
//			}
//			providerPKCS11 = null;
//			outputPublicKey = null;
//
//			try{
//				if(init(i) && eisTokenAlreadyFound)
//					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource("More than 1 eIS token detected, Please remove unsed.".getBytes()));
//				JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) outputPublicKey);
//				X500Name x500name = certHolder.getSubject();
//				RDN cn = x500name.getRDNs(BCStyle.CN)[0];
//				String commonName = IETFUtils.valueToString(cn.getFirst().getValue());
//				System.out.println("Common Name (CN): " + commonName);
//				out += "slot" + i + ": " + commonName + "\n";
//				eisTokenAlreadyFound = true;
//
//			}catch(Exception e){
//				String error = "Cannot identify the token";
//			}
//		}
//		return ResponseEntity.ok(out);
//	}


//	@CrossOrigin(origins = "*")
//	@RequestMapping(value = "signString", method = RequestMethod.POST)
//	public ResponseEntity signString(@RequestBody Map<String, Object> body) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
//		String inputString = (String) body.get("inputString");
//		String publicK = (String) body.get("publicK");
//		try{
//			return actualSignString(inputString, publicK);
//		}catch(Exception e){
//			logger.warn("Caught exception", e);
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource("Fail to sign. Please contact support.".getBytes()));
////			Security.removeProvider(providerPKCS11.getName());
////			providerPKCS11 = null;
////			return actualSignString(inputString, publicK);
//		}
//	}
//
//	public ResponseEntity actualSignString(String inputString,String publicK) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
////		if(providerPKCS11 == null){
////			if(!init()){
////				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource(this.initErrorMessage.getBytes()));
////			}
////		}
//
//		if(!comparePublicKeyCert(publicK))
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource("Public key is not match".getBytes()));
//
//		Signature signature = Signature.getInstance("SHA256withRSA");
//		signature.initSign(pk);
//		signature.update(inputString.getBytes(StandardCharsets.UTF_8));
//		byte[] sigValue = signature.sign();
//		String sigValueBase64 =  Base64.getEncoder().encodeToString(sigValue);
//
//		//verify
//		Signature signForVerify = Signature.getInstance("SHA256withRSA");
//		signForVerify.initVerify(outputPublicKey.getPublicKey());
//		signForVerify.update(inputString.getBytes(StandardCharsets.UTF_8));
//
//		return ResponseEntity.ok(sigValueBase64);
//	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "signString", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity signString(@RequestPart(name="json") String json,
	                               @RequestPart(name="cert") String publicK) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
		try{
			return actualSignString(json, publicK);
		}catch(Exception e){
			logger.warn("Caught exception", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessageDefault);
//			Security.removeProvider(providerPKCS11.getName());
//			providerPKCS11 = null;
//			return actualSignString(inputString, publicK);
		}
	}

	public ResponseEntity actualSignString(String json,String publicK) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
//		if(providerPKCS11 == null){
//			if(!init()){
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource(this.initErrorMessage.getBytes()));
//			}
//		}

		if (simulation) {
			try (FileOutputStream fos = new FileOutputStream(simulationUnsignedStringPath)) {
				fos.write(json.getBytes());
			}

			if (!simulationSilence) {
				System.out.println("1. Get unsigned JSON string from " + simulationUnsignedStringPath);
				System.out.println("2. Sign the JSON string");
				System.out.println("3. Place the signature to " + simulationSignedStringPath);
				System.out.println("4. Press Enter to continue..");
				Scanner scanner = new Scanner(System.in);
				String line = scanner.nextLine();
			}

			return ResponseEntity.ok().body(Files.readString(Path.of(simulationSignedStringPath)));
		}

		if(!comparePublicKeyCert(publicK))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageNotMatch);

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(pk);
		signature.update(json.getBytes(StandardCharsets.UTF_8));
		byte[] sigValue = signature.sign();
		String sigValueBase64 =  Base64.getEncoder().encodeToString(sigValue);

		//verify
		Signature signForVerify = Signature.getInstance("SHA256withRSA");
		signForVerify.initVerify(outputPublicKey.getPublicKey());
		signForVerify.update(json.getBytes(StandardCharsets.UTF_8));

		return ResponseEntity.ok(sigValueBase64);
	}

	@CrossOrigin(origins = "*")
	@PostMapping(value = "signPdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity signPdf(
			@RequestPart("unsignedPdf") MultipartFile file,
			@RequestPart(name="cert") String publicK,
			@RequestPart(name="reason", required=false) String reason,
			@RequestPart(name="location", required=false) String location,
			@RequestPart(name="qr", required=false) String qr,
			@RequestPart(name="keyword", required=false) String keyword,
			HttpServletResponse response
	) throws Exception {
		try{
			return actualSignPdf( file,  publicK,  reason,  location, qr, keyword, response);
		}catch(Exception e){
			logger.warn("Caught exception", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessageDefault);
//			Security.removeProvider(providerPKCS11.getName());
//			providerPKCS11 = null;
//			return actualSignPdf( file,  publicK,  reason,  location,  response);
		}
	}

	public ResponseEntity actualSignPdf(
			MultipartFile file, String publicK, String reason, String location,
			String qr, String keyword,
			HttpServletResponse response
	) throws Exception {

		if (simulation) {
			byte[] data = file.getBytes();
			response.setHeader("Content-Disposition", "attachment; filename=" + file.getOriginalFilename());

			FileOutputStream fos = new FileOutputStream(simulationUnsignedPdfPath);
			fos.write(file.getInputStream().readAllBytes());
			fos.close();

			if (!simulationSilence) {
				System.out.println("1. Get unsigned PDF from " + simulationUnsignedPdfPath);
				System.out.println("2. Add keyword to metadata and sign the PDF");
				System.out.println("3. Place the signed PDF to " + dest);
				System.out.println("4. Press Enter to continue..");
				Scanner scanner = new Scanner(System.in);
				String line = scanner.nextLine();
			}

			InputStreamResource resource = new InputStreamResource(new FileInputStream(dest));
			response.setHeader("Content-Disposition", "attachment; filename=" + file.getOriginalFilename());
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

			//return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new ByteArrayResource(data));
		}

//		if(providerPKCS11 == null){
//			if(!init()){
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource(this.initErrorMessage.getBytes()));
//			}
//		}

		if(!comparePublicKeyCert(publicK))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageNotMatch);

		// sign PDF
		pdfBoxSign signing = new pdfBoxSign(ks, pin.toCharArray());
		signing.setExternalSigning(false);

		signing.setTsaUrl(null);
		PDDocument doc = PDDocument.load(file.getInputStream());

		//boolean manualAddKeyword = true;
		if(!Strings.isNullOrEmpty(keyword)) {
			QRCodeWriter qrWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrWriter.encode(qr, BarcodeFormat.QR_CODE, 350, 350);

			BufferedImage qrImage = new BufferedImage(350, 350, BufferedImage.TYPE_INT_RGB);
			qrImage.createGraphics();

			Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
			graphics.setColor(java.awt.Color.WHITE);
			graphics.fillRect(0, 0, 350, 350);
			graphics.setColor(java.awt.Color.BLACK);

			for (int i = 0; i < 350; i++) {
				for (int j = 0; j < 350; j++) {
					if (bitMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
			PDPage page = doc.getPage(0);
// Add a new page to the document

			PDPageContentStream contentStream = new PDPageContentStream(doc, page);

// Convert BufferedImage to PDImageXObject
// Please adjust path to your actual requirement.
			PDImageXObject pdImage = LosslessFactory.createFromImage(doc, qrImage);

// Adjust the position (x,y) and the QR's size in the pdf
			contentStream.drawImage(pdImage, 100, 400, 200, 200);

// Closing the contentStream
			contentStream.close();

			PDDocumentInformation info = new PDDocumentInformation();
			info.setTitle("testing setTitle");
			info.setAuthor("testing setAuthor");
			info.setKeywords(keyword);

			doc.setDocumentInformation(info);

			PDDocumentCatalog catalog = doc.getDocumentCatalog();

			XMPMetadata metadata = XMPMetadata.createXMPMetadata();

			AdobePDFSchema pdfSchema = metadata.createAndAddAdobePDFSchema();
			pdfSchema.setKeywords( info.getKeywords() );

			DublinCoreSchema dcSchema = metadata.createAndAddDublinCoreSchema();
			dcSchema.setTitle( info.getTitle() );

			PDMetadata metadataStream = new PDMetadata(doc);
			catalog.setMetadata( metadataStream );

			XmpSerializer serializer = new XmpSerializer();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			serializer.serialize(metadata, baos, false);
			metadataStream.importXMPMetadata( baos.toByteArray() );

			// Save the changes to a new file
			doc.save("pdfwithMetadata.pdf");

			doc = PDDocument.load(new File("pdfwithMetadata.pdf"));
		}

		FileOutputStream os = new FileOutputStream(dest);
		signing.signDetached(doc, os, reason, location);

		InputStreamResource resource = new InputStreamResource(new FileInputStream(dest));
		//TODO remove local signed pdf after response
		response.setHeader("Content-Disposition", "attachment; filename=" + file.getOriginalFilename());
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

	}



//	public ResponseEntity<Resource> actualSignPdfiText(
//			MultipartFile file, String publicK, String reason, String location, HttpServletResponse response
//	) throws Exception {
//
//		if (!actualSign) {
//			byte[] data = file.getBytes();
//			response.setHeader("Content-Disposition", "attachment; filename=" + file.getOriginalFilename());
//			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new ByteArrayResource(data));
//		}
//
//		if(providerPKCS11 == null){
//			if(!init()){
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ByteArrayResource(this.initErrorMessage.getBytes()));
//			}
//		}
//
//		OcspClient ocspClient = new OcspClientBouncyCastle();
//		TSAClient tsaClient = null;
//
//		for (int i = 0; i < chain.length; i++) {
//			X509Certificate cert = (X509Certificate)chain[i];
//			String tsaUrl = CertificateUtil.getTSAURL(cert);
//			if (tsaUrl != null) {
//				tsaClient = new TSAClientBouncyCastle(tsaUrl);
//				break;
//			}
//		}
//
//		List<CrlClient> crlList = new ArrayList<CrlClient>();
//		crlList.add(new CrlClientOnline(chain));
//
//		PdfReader reader = new PdfReader(file.getInputStream());
//		FileOutputStream os = new FileOutputStream(dest);
//		PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
//
//		// Creating the appearance
//		PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
//		appearance.setReason(reason);
//		appearance.setLocation(location);
//
//		// Creating the signature
//		ExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, providerPKCS11.getName());
//		ExternalDigest digest = new BouncyCastleDigest();
//
//		System.out.println(pk.getEncoded());
//		System.out.println(pks.getEncryptionAlgorithm() + " - " + pks.getHashAlgorithm());
//
//		MakeSignature.signDetached(appearance, digest, pks, chain, crlList, ocspClient, tsaClient, 0, MakeSignature.CryptoStandard.CMS);
//
//		InputStreamResource resource = new InputStreamResource(new FileInputStream(dest));
//		//TODO remove local signed pdf after response
//		response.setHeader("Content-Disposition", "attachment; filename=" + file.getOriginalFilename());
//		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
//
//	}
}