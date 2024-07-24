package hk.gov.ogcio.eproof.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import hk.gov.ogcio.eproof.model.EProof;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PDFUtil {

    private static final Logger logger = LogManager.getLogger(PDFUtil.class);

    public static void convertDocxToPdf(String docxFilePath, String pdfFilePath, EProof eProof) throws Exception {
        //try {
            String eProofData = eProof.geteProofDataOutput();
            String qrCode = eProof.getQrCodeString();
            if (eProof.getQrCodeWidth() >0 && eProof.getQrCodeHeight() >0){
                QR_WIDTH = eProof.getQrCodeWidth();
                QR_HEIGHT = eProof.getQrCodeHeight();
            }
            String docxOutFilePath = docxFilePath.replace(".docx", "_out.docx");
            replaceTextInDocx(docxFilePath,docxOutFilePath,eProofData,qrCode);
            logger.info("conversion start");
            InputStream inputStream = new FileInputStream(docxOutFilePath);
            OutputStream outputStream = new FileOutputStream(pdfFilePath);

            XWPFDocument document = new XWPFDocument(inputStream);
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, outputStream, options);
            outputStream.close();
            inputStream.close();

            logger.info("conversion completion");
            File file = new File(docxOutFilePath);
            if (file.exists()){
                file.delete();
            }else {
                throw new IllegalArgumentException("File "+file+" is not exists");
            }
    //    } catch (IOException e) {
    //        throw new RuntimeException(e);
    //    }
    }

    private final static String QRCODE = "QR_CODE";

    public static void replaceTextInDocx(String docxInFilePath,String docxOutFilePath,String json,String qrCode){
        try {
            logger.info("Replacement start");
            FileInputStream fis = new FileInputStream(docxInFilePath);
            XWPFDocument document = new XWPFDocument(fis);
            Map<String, Object> map = readJson(json);
            if (map == null){
                throw new IllegalArgumentException("eproof_data is null! ");
            }
            //QR_CODE
            map.put(QRCODE,generateQrCode(qrCode));
            // Replace text in paragraphs
            replaceTextInParagraphs(document,map);
            // Replace text in tables
            replaceTextInTables(document,map);
            FileOutputStream fos = new FileOutputStream(docxOutFilePath);
            document.write(fos);
            fis.close();
            fos.close();
            document.close();

            logger.info("Replacement completed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void replaceTextInParagraphs(XWPFDocument document,Map<String,Object> map) {
        // Iterate through paragraphs
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceTextInRuns(paragraph,map);
        }
    }

    private static void replaceTextInTables(XWPFDocument document,Map<String,Object> map) {
        // Iterate through tables
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceTextInRuns(paragraph,map);
                    }
                }
            }
        }
    }

    private final static String IMAGE_BASE_64 ="image_base64";
    private final static int IMAGE_WIDTH = 160;
    private final static int IMAGE_HEIGHT = 160;
    private static int QR_WIDTH = 180;
    private static int QR_HEIGHT = 180;
    private static String ASCII = "Microsoft JhengHei";
    private final static Pattern PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");

    private static void replaceTextInRuns(XWPFParagraph paragraph,Map<String, Object> map) {
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.text();
            if (StringUtils.isNotBlank(text) && PATTERN.matcher(text).find()){
                ASCII = run.getFontFamily();
            }
        }
        if (matcher(paragraph.getParagraphText()).find()){
            List<XWPFRun> runs = replaceText(paragraph);
            for (XWPFRun run : runs) {
                String text = run.text();
                Pattern pattern = Pattern.compile(REGEX);
                Matcher runMatcher = pattern.matcher(text);
                if (runMatcher.find()) {
                    String key = runMatcher.group(1);
                    if (StringUtils.isNotBlank(key)) {
                        if (map != null && map.get(key) != null) {
                            String value = String.valueOf(map.get(key));
                            if (key.equals(IMAGE_BASE_64)) {
                                byte[] decode = Base64.getDecoder().decode(value);
                                try {
                                    run.setText("", 0);
                                    run.addPicture(new ByteArrayInputStream(decode), XWPFDocument.PICTURE_TYPE_JPEG, IMAGE_BASE_64 + ".jpg", Units.toEMU(IMAGE_WIDTH), Units.toEMU(IMAGE_HEIGHT));
                                } catch (InvalidFormatException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (key.equals(QRCODE)) {
                                byte[] decode = Base64.getDecoder().decode(value);
                                try {
                                    run.setText("", 0);
                                    run.addPicture(new ByteArrayInputStream(decode), XWPFDocument.PICTURE_TYPE_PNG, QRCODE + ".png", Units.toEMU(QR_WIDTH), Units.toEMU(QR_HEIGHT));
                                } catch (InvalidFormatException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                if (PATTERN.matcher(value).find()) {
                                    replaceFont(run);
                                }
                                run.setText(value, 0);
                            }
                        } else {
                            run.setText("",0);
                        }
                    }
                }
            }
        }
    }

    private static void replaceFont(XWPFRun run){
        run.setFontFamily(ASCII);
        CTFonts rFonts = run.getCTR().getRPr().getRFonts();
        rFonts.setAscii(ASCII);
        rFonts.setEastAsia(ASCII);
        rFonts.setHAnsi(ASCII);
        rFonts.setCs(ASCII);
    }

    private final static String REPLACE_REGEX = ".*\\[\\*\\*\\[.*|.*\\[.*|.*\\[\\*.*|.*\\[\\*\\*.*";

    private static List<XWPFRun> replaceText(XWPFParagraph paragraph){
        List<XWPFRun> runs = paragraph.getRuns();
        StringBuilder textBuilder = new StringBuilder();
        boolean flag = false;
        for (int i = 0;i<runs.size();i++){
            XWPFRun run = runs.get(i);
            String text = run.text();
            if (matcher((text)).find()){
                continue;
            }
            if (flag || text.matches(REPLACE_REGEX)) {
                textBuilder.append(text);
                flag = true;
                if (!matcher(textBuilder.toString()).find()){
                    paragraph.removeRun(i);
                    i--;
                }else {
                    run.setText(textBuilder.toString(),0);
                    textBuilder.setLength(0);
                    flag = false;
                }
            }
        }
        return runs;
    }

    private final static String REGEX = "\\[\\*\\*\\[(.*?)\\]\\*\\*\\]";
    private static Matcher matcher(String text) {
        Pattern pattern = Pattern.compile(REGEX,Pattern.CASE_INSENSITIVE);
        return pattern.matcher(text);
    }

    private static Map<String, Object> readJson(String json){
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("credentialSubject") && !jsonObject.isNull("credentialSubject")){
            JSONObject credentialSubject = jsonObject.getJSONObject("credentialSubject");
            if (credentialSubject.has("display") && !credentialSubject.isNull("display")){
                JSONObject display = credentialSubject.getJSONObject("display");
                return toMap(display);
            }
        }
        return null;
    }

    public static Map<String, Object> toMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static String generateQrCode(String qrCode){
        // Generate QR code image
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        final String fileType = "png";
        ByteArrayOutputStream code = new ByteArrayOutputStream();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
            MatrixToImageWriter.writeToStream(bitMatrix, fileType, code);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        // Encode QR code image to base64 string
        return Base64.getEncoder().encodeToString(code.toByteArray());
    }
}
