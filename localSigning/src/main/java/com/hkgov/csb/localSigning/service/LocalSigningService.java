package com.hkgov.csb.localSigning.service;


import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hkgov.csb.localSigning.util.ApiUtil;
import com.hkgov.csb.localSigning.util.PdfBoxSign;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class LocalSigningService {

    @Value("${path.dest}")
    private String dest;

    @Value("${path.config0}")
    private String configSlot0Name;
    @Value("${path.config1}")
    private String configSlot1Name;

    private final ApiUtil apiUtil;



    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Provider providerPKCS11;
    private PrivateKey pk;

    public Certificate getOutputPublicKey() {
        return outputPublicKey;
    }

    private Certificate outputPublicKey;
    private KeyStore ks;
    private HttpStatus initResponseCode;
    private String commonName;
    private String pin ="";

    public LocalSigningService(ApiUtil apiUtil){
        this.apiUtil = apiUtil;
    }

    public String getCommonName() {
        return commonName;
    }

    public boolean init() {
        try{


            if(providerPKCS11 != null){
                Security.removeProvider(providerPKCS11.getName());
                providerPKCS11 = null;
                ks = null;
                pk = null;
                outputPublicKey = null;
                commonName = null;
            }

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

    @Async("normalThreadPool")
    public void processSignAndIssue(String jwt,
                                    String reason,
                                    String location,
                                    String qr,
                                    String keyword,
                                    HttpServletResponse response,
                                    String publicKey, Long nextCertInfoIdForSigning) throws Exception {

        String unsignedJson = apiUtil.getUnsignedJsonForCert(nextCertInfoIdForSigning,jwt);
        String signedValue = (String)this.signJson(unsignedJson).getBody();
        logger.info(signedValue);
        byte[] preparedPdf = apiUtil.prepareEproofPdfForSigning(jwt,nextCertInfoIdForSigning,unsignedJson,signedValue, publicKey);

        this.processSigning(preparedPdf, nextCertInfoIdForSigning,jwt,reason,  location, qr, keyword, response,publicKey);
    }

    private void processSigning(byte[] preparedPdfBinaryArray, Long nextCertInfoIdForSigning, String jwtTokenFromFrontEnd, String reason, String location, String qr, String keyword, HttpServletResponse response, String publicKey) throws Exception {
        byte[] signedPdf = this.getSignedPdf(new ByteArrayInputStream(preparedPdfBinaryArray), publicKey, reason, location, qr, keyword);
        apiUtil.uploadSignedPdf(nextCertInfoIdForSigning,jwtTokenFromFrontEnd,signedPdf);
    }


    public byte[] getSignedPdf(
            InputStream is, String publicK, String reason, String location,
            String qr, String keyword
    ) throws Exception {

        /*if (simulation) {
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
        }*/


        if(!comparePublicKeyCert(publicK))
            throw new Exception("errorMessageNotMatch");

        // sign PDF
        PdfBoxSign signing = new PdfBoxSign(ks, pin.toCharArray());
        signing.setExternalSigning(false);

        signing.setTsaUrl(null);
        PDDocument doc = PDDocument.load(is);

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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signing.signDetached(doc, baos, reason, location);

//        InputStreamResource resource = new InputStreamResource(new FileInputStream(dest));
        //TODO remove local signed pdf after response
        doc.close();
        baos.close();
        return baos.toByteArray();
    }


    public String getSigningCert() throws CertificateEncodingException {

/*
        if(providerPKCS11 != null){
            Security.removeProvider(providerPKCS11.getName());
            providerPKCS11 = null;
            ks = null;
            pk = null;
            outputPublicKey = null;
            commonName = null;
        }

        */

        if(outputPublicKey == null){
            this.init();
        }

        String publicKeyCert = Base64.getEncoder().encodeToString(outputPublicKey.getEncoded());
        String publicKeyFormatted = "-----BEGIN PUBLIC KEY-----" + "\r\n";
        for (final String row: Splitter.fixedLength(64).split(publicKeyCert))
        {
            publicKeyFormatted += row + "\r\n";
        }
        publicKeyFormatted += "-----END PUBLIC KEY-----";

        return Base64.getEncoder().encodeToString(publicKeyFormatted.getBytes());
    }


    public ResponseEntity signJson(String json) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, CertificateEncodingException {

        String publicKey = this.getSigningCert();


        if(!comparePublicKeyCert(publicKey))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("errorMessageNotMatch");

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

    public void processReissue(String jwt, String reason, String location, String qr, String keyword, HttpServletResponse response, String publicKey, Long certInfoRenewId) throws Exception {
        String unsignedJson = apiUtil.getUnsignedJsonForReissueCert(certInfoRenewId,jwt);
        String signedValue = (String)this.signJson(unsignedJson).getBody();
        logger.info(signedValue);
        byte[] preparedPdf = apiUtil.prepareEproofPdfForSigningForReissueCert(jwt,certInfoRenewId,unsignedJson,signedValue);

        this.processSigningForReissueCert(preparedPdf, certInfoRenewId,jwt,reason,  location, qr, keyword, response,publicKey);

    }

    private void processSigningForReissueCert(byte[] preparedPdf, Long certInfoRenewId, String jwt, String reason, String location, String qr, String keyword, HttpServletResponse response, String publicKey) throws Exception {
        byte[] signedPdf = this.getSignedPdf(new ByteArrayInputStream(preparedPdf), publicKey, reason, location, qr, keyword);
        apiUtil.uploadSignedPdfForReissueCert(certInfoRenewId,jwt,signedPdf);

    }
}
