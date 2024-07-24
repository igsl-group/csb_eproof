package hk.gov.ogcio.eproof.controller.pdf;

import hk.gov.ogcio.eproof.controller.pdf.types.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Locale;

public final class BasicSignerOptions {

    private String ksType;
    private String ksFile;
    private char[] ksPasswd;
    private String keyAlias;
    private int keyIndex = Constants.DEFVAL_KEY_INDEX;
    private char[] keyPasswd;
    private String inFile;
    private String outFile;
    private String signerName;
    private String reason;
    private String location;
    private String contact;
    private SignResultListener listener;
    private boolean append = Constants.DEFVAL_APPEND;
    private boolean advanced;
    private PDFEncryption pdfEncryption;
    private char[] pdfOwnerPwd;
    private char[] pdfUserPwd;
    private String pdfEncryptionCertFile;
    private CertificationLevel certLevel;
    private HashAlgorithm hashAlgorithm;

    protected boolean storePasswords;

    // options from rights dialog
    private PrintRight rightPrinting;
    private boolean rightCopy;
    private boolean rightAssembly;
    private boolean rightFillIn;
    private boolean rightScreenReaders;
    private boolean rightModifyAnnotations;
    private boolean rightModifyContents;

    // options from visible signature settings dialog
    private boolean visible;
    private int page = Constants.DEFVAL_PAGE;
    private float positionLLX = Constants.DEFVAL_LLX;
    private float positionLLY = Constants.DEFVAL_LLY;
    private float positionURX = Constants.DEFVAL_URX;
    private float positionURY = Constants.DEFVAL_URY;
    private float bgImgScale = Constants.DEFVAL_BG_SCALE;
    private RenderMode renderMode;
    private String l2Text;
    private String l4Text;
    private float l2TextFontSize = Constants.DEFVAL_L2_FONT_SIZE;
    private String imgPath;
    private String bgImgPath;
    private boolean acro6Layers = Constants.DEFVAL_ACRO6LAYERS;

    // options for timestamps (provided by external TSA)
    private boolean timestamp;
    private String tsaUrl;
    private ServerAuthentication tsaServerAuthn;
    private String tsaUser;
    private String tsaPasswd;
    private String tsaCertFileType;
    private String tsaCertFile;
    private String tsaCertFilePwd;
    private String tsaPolicy;
    private String tsaHashAlg;

    // options for certificate validation
    private boolean ocspEnabled;
    private String ocspServerUrl;
    private boolean crlEnabled;

    // Proxy connection
    private Proxy.Type proxyType;
    private String proxyHost;
    private int proxyPort;

    private String[] cmdLine;



    /**
     * Fires event listener
     *
     * @param aResult
     * @see #getListener()
     */
    protected void fireSignerFinishedEvent(final Throwable aResult) {
        if (listener != null) {
            listener.signerFinishedEvent(aResult);
        }
    }

    /**
     * Converts array of characters to String. If array is null, empty string is returned
     *
     * @param aCharArr char array
     * @return not null string
     */
    private String charArrToStr(final char[] aCharArr) {
        return aCharArr == null ? "" : new String(aCharArr);
    }


    public String getKsType() {
        return ksType;
    }

    public void setKsType(final String ksType) {
        this.ksType = ksType;
    }

    public String getKsFile() {
        return ksFile;
    }

    public void setKsFile(final String ksFile) {
        this.ksFile = ksFile;
    }

    public char[] getKsPasswd() {
        return ksPasswd;
    }

    public String getKsPasswdStr() {
        return charArrToStr(ksPasswd);
    }

    public void setKsPasswd(final char[] passwd) {
        this.ksPasswd = passwd;
    }

    public void setKsPasswd(final String aPasswd) {
        setKsPasswd(aPasswd == null ? null : aPasswd.toCharArray());
    }

    public String getInFile() {
        return inFile;
    }

    public void setInFile(final String inFile) {
        this.inFile = inFile;
    }

    public String getOutFile() {
        return outFile;
    }

    /**
     * Returns output file name if filled or input file name with default output suffix ("_signed")
     *
     * @return
     */
    public String getOutFileX() {
        String tmpOut = StringUtils.defaultIfBlank(outFile, null);
        if (tmpOut == null) {
            String tmpExtension = "";
            String tmpNameBase = StringUtils.defaultIfBlank(getInFile(), null);
            if (tmpNameBase == null) {
                tmpOut = "signed.pdf";
            } else {
                if (tmpNameBase.toLowerCase().endsWith(".pdf")) {
                    final int tmpBaseLen = tmpNameBase.length() - 4;
                    tmpExtension = tmpNameBase.substring(tmpBaseLen);
                    tmpNameBase = tmpNameBase.substring(0, tmpBaseLen);
                }
                tmpOut = tmpNameBase + Constants.DEFAULT_OUT_SUFFIX + tmpExtension;
            }
        }
        return tmpOut;
    }

    public void setOutFile(final String outFile) {
        this.outFile = outFile;
    }

    public String getSignerName() {
        return signerName;
    }

    public void setSignerName(final String signerName) {
        this.signerName = signerName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public SignResultListener getListener() {
        return listener;
    }

    public void setListener(final SignResultListener listener) {
        this.listener = listener;
    }

    public char[] getKeyPasswd() {
        return keyPasswd;
    }

    public char[] getKeyPasswdX() {
        if (keyPasswd != null && keyPasswd.length == 0) {
            keyPasswd = null;
        }
        return (advanced && keyPasswd != null) ? keyPasswd : ksPasswd;
    }

    public String getKeyPasswdStr() {
        return charArrToStr(keyPasswd);
    }

    public void setKeyPasswd(final char[] keyPasswd) {
        this.keyPasswd = keyPasswd;
    }

    public void setKeyPasswd(final String aPasswd) {
        setKeyPasswd(aPasswd == null ? null : aPasswd.toCharArray());
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getKeyAliasX() {
        return advanced ? keyAlias : null;
    }

    public void setKeyAlias(final String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public int getKeyIndexX() {
        return advanced ? keyIndex : Constants.DEFVAL_KEY_INDEX;
    }

    public void setKeyIndex(final int anIndex) {
        this.keyIndex = anIndex;
        if (keyIndex < 0)
            keyIndex = Constants.DEFVAL_KEY_INDEX;
    }

    public boolean isAppend() {
        return append;
    }

    public boolean isAppendX() {
        return (getPdfEncryption() == PDFEncryption.NONE)
                && ((!Constants.DEFVAL_APPEND && advanced && append) || (Constants.DEFVAL_APPEND && (append || !advanced)));
    }

    public void setAppend(final boolean append) {
        this.append = append;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public void setAdvanced(final boolean advanced) {
        this.advanced = advanced;
    }

    /**
     * @return the pdfEncryption
     */
    public PDFEncryption getPdfEncryption() {
        if (pdfEncryption == null) {
            pdfEncryption = PDFEncryption.NONE;
        }
        return pdfEncryption;
    }

    /**
     * @param pdfEncryption the pdfEncryption to set
     */
    public void setPdfEncryption(final PDFEncryption pdfEncryption) {
        this.pdfEncryption = pdfEncryption;
    }

    public void setPdfEncryption(final String aValue) {
        PDFEncryption enumInstance = null;
        if (aValue != null) {
            try {
                enumInstance = PDFEncryption.valueOf(aValue.toUpperCase(Locale.ENGLISH));
            } catch (final Exception e) {
                // probably illegal value - fallback to a default (i.e. null)
            }
        }
        setPdfEncryption(enumInstance);
    }

    public char[] getPdfOwnerPwd() {
        return pdfOwnerPwd;
    }

    public String getPdfOwnerPwdStr() {
        return charArrToStr(pdfOwnerPwd);
    }

    public String getPdfOwnerPwdStrX() {
        return charArrToStr(advanced ? pdfOwnerPwd : null);
    }

    public void setPdfOwnerPwd(final char[] pdfOwnerPwd) {
        this.pdfOwnerPwd = pdfOwnerPwd;
    }

    /**
     * @return the pdfEncryptionCertFile
     */
    public String getPdfEncryptionCertFile() {
        return pdfEncryptionCertFile;
    }

    /**
     * @param pdfEncryptionCertFile the pdfEncryptionCertFile to set
     */
    public void setPdfEncryptionCertFile(final String pdfEncryptionCertFile) {
        this.pdfEncryptionCertFile = pdfEncryptionCertFile;
    }

    public void setPdfOwnerPwd(final String aPasswd) {
        setPdfOwnerPwd(aPasswd == null ? null : aPasswd.toCharArray());
    }

    public char[] getPdfUserPwd() {
        return pdfUserPwd;
    }

    public String getPdfUserPwdStr() {
        return charArrToStr(pdfUserPwd);
    }

    public void setPdfUserPwd(final char[] pdfUserPwd) {
        this.pdfUserPwd = pdfUserPwd;
    }

    public void setPdfUserPwd(final String aPasswd) {
        setPdfUserPwd(aPasswd == null ? null : aPasswd.toCharArray());
    }

    public CertificationLevel getCertLevel() {
        if (certLevel == null) {
            certLevel = CertificationLevel.NOT_CERTIFIED;
        }
        return certLevel;
    }

    public CertificationLevel getCertLevelX() {
        return advanced ? getCertLevel() : CertificationLevel.NOT_CERTIFIED;
    }

    public void setCertLevel(final CertificationLevel aCertLevel) {
        this.certLevel = aCertLevel;
    }

    public void setCertLevel(final String aValue) {
        CertificationLevel certLevel = null;
        if (aValue != null) {
            try {
                certLevel = CertificationLevel.valueOf(aValue.toUpperCase(Locale.ENGLISH));
            } catch (final Exception e) {
                // probably illegal value - fallback to a default (i.e. null)
            }
        }
        setCertLevel(certLevel);
    }

    public boolean isRightCopy() {
        return rightCopy;
    }

    public void setRightCopy(final boolean rightCopy) {
        this.rightCopy = rightCopy;
    }

    public boolean isRightAssembly() {
        return rightAssembly;
    }

    public void setRightAssembly(final boolean rightAssembly) {
        this.rightAssembly = rightAssembly;
    }

    public boolean isRightFillIn() {
        return rightFillIn;
    }

    public void setRightFillIn(final boolean rightFillIn) {
        this.rightFillIn = rightFillIn;
    }

    public boolean isRightScreenReaders() {
        return rightScreenReaders;
    }

    public void setRightScreenReaders(final boolean rightScreenReaders) {
        this.rightScreenReaders = rightScreenReaders;
    }

    public boolean isRightModifyAnnotations() {
        return rightModifyAnnotations;
    }

    public void setRightModifyAnnotations(final boolean rightModifyAnnotations) {
        this.rightModifyAnnotations = rightModifyAnnotations;
    }

    public boolean isRightModifyContents() {
        return rightModifyContents;
    }

    public void setRightModifyContents(final boolean rightModifyContents) {
        this.rightModifyContents = rightModifyContents;
    }

    public PrintRight getRightPrinting() {
        if (rightPrinting == null) {
            rightPrinting = PrintRight.ALLOW_PRINTING;
        }
        return rightPrinting;
    }

    public void setRightPrinting(PrintRight rightPrinting) {
        this.rightPrinting = rightPrinting;
    }

    public void setRightPrinting(final String aValue) {
        PrintRight printRight = null;
        if (aValue != null) {
            try {
                printRight = PrintRight.valueOf(aValue.toUpperCase(Locale.ENGLISH));
            } catch (final Exception e) {
                // probably illegal value - fallback to a default (i.e. null)
            }
        }
        setRightPrinting(printRight);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int aPage) {
        this.page = aPage;
    }

    public float getPositionLLX() {
        return positionLLX;
    }

    public void setPositionLLX(final float positionLLX) {
        this.positionLLX = positionLLX;
    }

    public float getPositionLLY() {
        return positionLLY;
    }

    public void setPositionLLY(final float positionLLY) {
        this.positionLLY = positionLLY;
    }

    public float getPositionURX() {
        return positionURX;
    }

    public void setPositionURX(final float positionURX) {
        this.positionURX = positionURX;
    }

    public float getPositionURY() {
        return positionURY;
    }

    public void setPositionURY(final float positionURY) {
        this.positionURY = positionURY;
    }

    public float getBgImgScale() {
        return bgImgScale;
    }

    public void setBgImgScale(final float bgImgScale) {
        this.bgImgScale = bgImgScale;
    }

    public RenderMode getRenderMode() {
        if (renderMode == null) {
            renderMode = RenderMode.DESCRIPTION_ONLY;
        }
        return renderMode;
    }

    public void setRenderMode(final RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    public void setRenderMode(final String aValue) {
        RenderMode renderMode = null;
        if (aValue != null) {
            try {
                renderMode = RenderMode.valueOf(aValue.toUpperCase(Locale.ENGLISH));
            } catch (final Exception e) {
                // probably illegal value - fallback to default (i.e. null)
            }
        }
        setRenderMode(renderMode);
    }

    public String getL2Text() {
        return l2Text;
    }

    public void setL2Text(final String text) {
        l2Text = text;
    }

    public String getL4Text() {
        return l4Text;
    }

    public void setL4Text(final String text) {
        l4Text = text;
    }

    public String getImgPath() {
        return (imgPath = StringUtils.defaultIfBlank(imgPath, null));
    }

    public void setImgPath(final String imgPath) {
        this.imgPath = imgPath;
    }

    public String getBgImgPath() {
        return (bgImgPath = StringUtils.defaultIfBlank(bgImgPath, null));
    }

    public void setBgImgPath(final String bgImgPath) {
        this.bgImgPath = bgImgPath;
    }

    /**
     * @return the l2TextFontSize
     */
    public float getL2TextFontSize() {
        if (l2TextFontSize <= 0f) {
            l2TextFontSize = Constants.DEFVAL_L2_FONT_SIZE;
        }
        return l2TextFontSize;
    }

    /**
     * @param textFontSize the l2TextFontSize to set
     */
    public void setL2TextFontSize(final float textFontSize) {
        l2TextFontSize = textFontSize;
    }

    /**
     * @return the acro6Layers
     */
    public boolean isAcro6Layers() {
        return acro6Layers;
    }

    /**
     * @param acro6Layers the acro6Layers to set
     */
    public void setAcro6Layers(final boolean acro6Layers) {
        this.acro6Layers = acro6Layers;
    }



    /**
     * @return the timestamp
     */
    public boolean isTimestamp() {
        return timestamp;
    }

    public boolean isTimestampX() {
        return advanced && timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(final boolean timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the tsaUrl
     */
    public String getTsaUrl() {
        return tsaUrl;
    }

    /**
     * @param tsaUrl the tsaUrl to set
     */
    public void setTsaUrl(final String tsaUrl) {
        this.tsaUrl = tsaUrl;
    }

    /**
     * @return the tsaUser
     */
    public String getTsaUser() {
        return tsaUser;
    }

    /**
     * @param tsaUser the tsaUser to set
     */
    public void setTsaUser(final String tsaUser) {
        this.tsaUser = tsaUser;
    }

    /**
     * @return the tsaServerAuthn
     */
    public ServerAuthentication getTsaServerAuthn() {
        if (tsaServerAuthn == null) {
            tsaServerAuthn = ServerAuthentication.NONE;
        }
        return tsaServerAuthn;
    }

    /**
     * @param tsaServerAuthn the tsaServerAuthn to set
     */
    public void setTsaServerAuthn(final ServerAuthentication tsaServerAuthn) {
        this.tsaServerAuthn = tsaServerAuthn;
    }

    /**
     * @param aValue
     */
    public void setTsaServerAuthn(final String aValue) {
        ServerAuthentication enumInstance = null;
        if (aValue != null) {
            try {
                enumInstance = ServerAuthentication.valueOf(aValue.toUpperCase(Locale.ENGLISH));
            } catch (final Exception e) {
                // probably illegal value - fallback to a default (i.e. null)
            }
        }
        setTsaServerAuthn(enumInstance);
    }

    /**
     * @return the tsaCertFileType
     */
    public String getTsaCertFileType() {
        return tsaCertFileType;
    }

    /**
     * @param tsaCertFileType the tsaCertFileType to set
     */
    public void setTsaCertFileType(String tsaCertFileType) {
        this.tsaCertFileType = tsaCertFileType;
    }

    /**
     * @return the tsaCertFile
     */
    public String getTsaCertFile() {
        return tsaCertFile;
    }

    /**
     * @param tsaCertFile the tsaCertFile to set
     */
    public void setTsaCertFile(final String tsaCertFile) {
        this.tsaCertFile = tsaCertFile;
    }

    /**
     * @return the tsaCertFilePwd
     */
    public String getTsaCertFilePwd() {
        return tsaCertFilePwd;
    }

    /**
     * @param tsaCertFilePwd the tsaCertFilePwd to set
     */
    public void setTsaCertFilePwd(final String tsaCertFilePwd) {
        this.tsaCertFilePwd = tsaCertFilePwd;
    }

    /**
     * @return the tsaPolicy
     */
    public String getTsaPolicy() {
        return tsaPolicy;
    }

    /**
     * @param tsaPolicy the tsaPolicy to set
     */
    public void setTsaPolicy(final String tsaPolicy) {
        this.tsaPolicy = tsaPolicy;
    }

    /**
     * @return the tsaHashAlg
     */
    public String getTsaHashAlg() {
        return tsaHashAlg;
    }

    /**
     * @return
     */
    public String getTsaHashAlgWithFallback() {
        return StringUtils.defaultIfBlank(tsaHashAlg, Constants.DEFVAL_TSA_HASH_ALG);
    }

    /**
     * @param tsaHashAlg the tsaHashAlg to set
     */
    public void setTsaHashAlg(String tsaHashAlg) {
        this.tsaHashAlg = tsaHashAlg;
    }

    /**
     * @return the tsaPasswd
     */
    public String getTsaPasswd() {
        return tsaPasswd;
    }

    /**
     * @param tsaPasswd the tsaPasswd to set
     */
    public void setTsaPasswd(final String tsaPasswd) {
        this.tsaPasswd = tsaPasswd;
    }

    /**
     * @return the ocspEnabled
     */
    public boolean isOcspEnabled() {
        return ocspEnabled;
    }

    public boolean isOcspEnabledX() {
        return advanced && ocspEnabled;
    }

    /**
     * @param ocspEnabled the ocspEnabled to set
     */
    public void setOcspEnabled(final boolean ocspEnabled) {
        this.ocspEnabled = ocspEnabled;
    }

    /**
     * @return the ocspServerUrl
     */
    public String getOcspServerUrl() {
        return ocspServerUrl;
    }

    /**
     * @param ocspServerUrl the ocspServerUrl to set
     */
    public void setOcspServerUrl(final String ocspServerUrl) {
        this.ocspServerUrl = ocspServerUrl;
    }

    public boolean isStorePasswords() {
        return storePasswords;
    }

    public void setStorePasswords(final boolean storePasswords) {
        this.storePasswords = storePasswords;
    }

    /**
     * @return the contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(final String contact) {
        this.contact = contact;
    }

    public boolean isCrlEnabled() {
        return crlEnabled;
    }

    public boolean isCrlEnabledX() {
        return advanced && crlEnabled;
    }

    public void setCrlEnabled(final boolean crlEnabled) {
        this.crlEnabled = crlEnabled;
    }

    public HashAlgorithm getHashAlgorithm() {
        if (hashAlgorithm == null) {
            hashAlgorithm = Constants.DEFVAL_HASH_ALGORITHM;
        }
        return hashAlgorithm;
    }

    public HashAlgorithm getHashAlgorithmX() {
        if (!advanced) {
            return Constants.DEFVAL_HASH_ALGORITHM;
        }
        return getHashAlgorithm();
    }

    public void setHashAlgorithm(final HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public void setHashAlgorithm(final String aValue) {
        HashAlgorithm hashAlg = null;
        if (StringUtils.isNotEmpty(aValue)) {
            try {
                hashAlg = HashAlgorithm.valueOf(aValue.toUpperCase(Locale.ENGLISH));
            } catch (final Exception e) {
                // probably illegal value - fallback to a default (i.e. null)
            }
        }
        setHashAlgorithm(hashAlg);
    }

    public Proxy.Type getProxyType() {
        if (proxyType == null) {
            proxyType = Constants.DEFVAL_PROXY_TYPE;
        }
        return proxyType;
    }

    public void setProxyType(final Proxy.Type proxyType) {
        this.proxyType = proxyType;
    }

    public void setProxyType(final String aValue) {
        Proxy.Type proxy = null;
        if (StringUtils.isNotEmpty(aValue)) {
            try {
                proxy = Proxy.Type.valueOf(aValue.toUpperCase(Locale.ENGLISH));
            } catch (final Exception e) {
                // probably illegal value - fallback to a default (i.e. null)
            }
        }
        setProxyType(proxy);
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * Creates and returns Proxy object, which should be used for URL connections in JSignPdf.
     *
     * @return initialized Proxy object.
     */
    public Proxy createProxy() {
        Proxy tmpResult = Proxy.NO_PROXY;
        if (isAdvanced() && getProxyType() != Proxy.Type.DIRECT) {
            tmpResult = new Proxy(getProxyType(), new InetSocketAddress(getProxyHost(), getProxyPort()));
        }
        return tmpResult;
    }

    protected String[] getCmdLine() {
        return cmdLine;
    }

    protected void setCmdLine(String[] cmdLine) {
        this.cmdLine = cmdLine;
    }

}
