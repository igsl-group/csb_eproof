package hk.gov.ogcio.eproof.controller;


import hk.gov.ogcio.eproof.model.SysObj;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class SysUtil {
    private static final Logger logger = LogManager.getLogger(SysUtil.class);

    //Init Methods
    public static String init(SysObj sysObj) throws Exception{
        String ErrMsg = "";
        SysUtil sysUtil = new SysUtil();
        try {
            // Try new method createDirectorIfNotExist(sysObj.getRootPath());
            initRootFolderIfNotExist(sysObj.getRootPath(), sysObj.getIDEMode());

            //Read Config File (JSON file)
            JSONObject jSysConfig = sysUtil.loadsysconfigJSON();
            if (jSysConfig == null) {
                ErrMsg = "Invalid config.json file";
            } else {
                sysObj.setSysConfig(jSysConfig);
            }

            //Read B/D Config File (JSON file)
            sysUtil.loadBDconfigJSON(sysObj);
            if (!sysUtil.loadBDconfigJSON(sysObj)) {
                ErrMsg = "Invalid BD Config file";
            }

            //API Health Check
            if (!ApiUtil.getHealthCheck(sysObj)) {
                ErrMsg = "API Health Check Error";

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            ErrMsg = "Exception + e.printStackTrace()";
            throw e;
        }

        return ErrMsg;
    }

    private static void initRootFolderIfNotExist(String str_path,boolean isNotJarFile) throws Exception{
        File directory = new File(str_path);
        try {
            if (!directory.exists()) {
                boolean isCreated = directory.mkdirs();
                if (isCreated) {
                    System.out.println("Directory created successfully at " + str_path);
                    // Copy Recurrsive files
                    //Path targetDir = Paths.get(str_path);
                    URL sourceURL = SysUtil.class.getClassLoader().getResource("init");
                    //Path sourceDir = Paths.get(sourceURL.toURI());
                    if (isNotJarFile){
                        FileUtils.copyDirectory(new File(sourceURL.toURI()), new File(str_path));
                    }else {
                        copyJarResourceToFolder((JarURLConnection) sourceURL.openConnection(),new File(str_path));
                    }
                } else {
                    System.out.println("Failed to create directory at " + str_path);
                }


            } else {
                System.out.println("Directory already exists. at " + str_path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }
    }

    public static void copyJarResourceToFolder(JarURLConnection jarConnection, File destDir) throws Exception {
        try {
            JarFile jarFile = jarConnection.getJarFile();
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                JarEntry jarEntry = e.nextElement();
                String jarEntryName = jarEntry.getName();
                String jarConnectionEntryName = jarConnection.getEntryName();
                if (jarEntryName.startsWith(jarConnectionEntryName)) {
                    String filename = jarEntryName.startsWith(jarConnectionEntryName) ? jarEntryName.substring(jarConnectionEntryName.length()) : jarEntryName;
                    File currentFile = new File(destDir, filename);
                    if (jarEntry.isDirectory()) {
                        currentFile.mkdirs();
                    } else {
                        InputStream is = jarFile.getInputStream(jarEntry);
                        OutputStream out = FileUtils.openOutputStream(currentFile);
                        IOUtils.copy(is, out);
                        is.close();
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static JSONObject loadJSONFile(String filepath) throws Exception {
        JSONObject jo = null;
        try {

            File file = new File(filepath);

            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String jsonContents = stringBuilder.toString();
            jo = new JSONObject(jsonContents);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }
        return (jo);
    }


    private JSONObject loadsysconfigJSON() throws Exception{
        JSONObject jo = null;
        try {
            InputStream in = SysUtil.class.getClassLoader().getResourceAsStream("sysconfig.json");

            StringBuilder stringBuilder = new StringBuilder();
            try {
                assert in != null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String jsonContents = stringBuilder.toString();
            JSONObject jsonObject = new JSONObject(jsonContents);
            jo = jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            jo = null;
            throw e;
        }
        return (jo);
    }
    public Boolean loadBDconfigJSON(SysObj sysobj)throws Exception {
        JSONObject jo = null;
        Boolean ret = false;
        try {
            String bdconfPath = "";


            JSONObject json = getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig.files[name=bdconfigfile]");
            if (json != null){
                bdconfPath = sysobj.getRootPath()
                        + File.separator
                        + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig").getString("foldername")
                        + File.separator
                        + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig.files[name=bdconfigfile]").getString("filename");
            }


            if (bdconfPath.length()> 0) {  //Valid Path
                File file = new File(bdconfPath);
                StringBuilder stringBuilder = new StringBuilder();
                if (!file.exists()) {
                    logger.debug("BD Config file not exist, copy the sample one from the package");
                    // Copy sample to that file
                    InputStream in = SysUtil.class.getClassLoader().getResourceAsStream("init/bdconfig/bdconfig.json");

                    /*
                    //Copy Dummy Check private key file when not exist
                    String str_filefullpath = bdconfPath = sysobj.getRootPath()
                            + File.separator
                            + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig").getString("foldername")
                            + File.separator
                            + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig.files[name=privatekey]").getString("filename");
                    if (ValidateFileExist(str_filefullpath) == false){
                        URL resource2 = getClass().getClassLoader().getResource("init/bdconfig/privateKey.pem");
                        Path sourcePath2 = Paths.get(resource2.toURI());
                        Path destinationPath2 = Paths.get(str_filefullpath);
                        Files.copy(sourcePath2, destinationPath2);
                    }

                    //Check Public Key
                    str_filefullpath = bdconfPath = sysobj.getRootPath()
                            + File.separator
                            + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig").getString("foldername")
                            + File.separator
                            + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig.files[name=publickey]").getString("filename");

                    if (ValidateFileExist(str_filefullpath) == false){
                        URL resource2 = getClass().getClassLoader().getResource("init/bdconfig/publicKey.pem");
                        Path sourcePath2 = Paths.get(resource2.toURI());
                        Path destinationPath2 = Paths.get(str_filefullpath);
                        Files.copy(sourcePath2, destinationPath2);
                    }

                    //Check BD logo
                    str_filefullpath = bdconfPath = sysobj.getRootPath()
                            + File.separator
                            + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig").getString("foldername")
                            + File.separator
                            + getJSONFieldShortcut(sysobj.getSysConfig(),"subfolder_structure.bdconfig.files[name=bdlogo]").getString("filename");
                    if (ValidateFileExist(str_filefullpath) == false){
                        URL resource2 = getClass().getClassLoader().getResource("init/bdconfig/bdlogo.png");
                        Path sourcePath2 = Paths.get(resource2.toURI());
                        Path destinationPath2 = Paths.get(str_filefullpath);
                        Files.copy(sourcePath2, destinationPath2);
                    }*/
                    try {
                        assert in != null;
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String jsonContents = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(jsonContents);
                sysobj.setBdConfig(jsonObject);



                ret = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            sysobj.setBdConfig(null);
            ret = false;
            throw e;
        }
        return ret;
    }

    public static JSONObject getJSONFieldShortcut(JSONObject jsonObj, String str_JSONPath) throws Exception {
        JSONObject jsonObjCur = null;
        //logger.debug("JSONObject -> " + str_JSONPath);

        try {
            String [] array_JSONPath = str_JSONPath.split("\\.");
            jsonObjCur = jsonObj;

            for (String str_JSONPathcur : array_JSONPath){
                if (str_JSONPathcur.contains("[")){
                    logger.debug("Test [Array]" + str_JSONPathcur);

                    String str_arrayName = str_JSONPathcur.substring(0, str_JSONPathcur.indexOf("["));
                    String str_arrayKeyField = str_JSONPathcur.substring(str_JSONPathcur.indexOf("[")+1, str_JSONPathcur.indexOf("="));
                    String str_arrayKeyValue = str_JSONPathcur.substring(str_JSONPathcur.indexOf("=")+1, str_JSONPathcur.length()-1);

                    JSONArray ja = jsonObjCur.getJSONArray(str_arrayName);
                    int len = ja.length();

                    ArrayList<String> files_names = new ArrayList<>();
                    Boolean isValueFound = false;
                    for(int j=0; j<len; j++){
                        JSONObject json = ja.getJSONObject(j);
                        if (str_arrayKeyValue.equals(json.getString(str_arrayKeyField))){
                            //Found object
                            jsonObjCur = json;
                            isValueFound = true;
                            break;
                        }
                    }
                    if (!isValueFound){
                        jsonObjCur = null; //make it null and let it throw exception
                    }
                }else{
                    logger.debug("Test [Object]" + str_JSONPathcur);
                    //Read as object
                    jsonObjCur = jsonObjCur.getJSONObject(str_JSONPathcur);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("JSON object not found in " + str_JSONPath);
            logger.error(e);
            jsonObjCur = null;
            throw e;
        }
        return jsonObjCur;
    }
}