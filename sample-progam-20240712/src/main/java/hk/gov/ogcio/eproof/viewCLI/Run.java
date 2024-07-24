package hk.gov.ogcio.eproof.viewCLI;

import hk.gov.ogcio.eproof.controller.Issuer;
import hk.gov.ogcio.eproof.controller.SysUtil;
import hk.gov.ogcio.eproof.model.SysObj;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

import static hk.gov.ogcio.eproof.controller.CommonUtil.readFileAsString;

public final class Run {
    private static final Logger logger = LogManager.getLogger(Run.class);

    public static void main(String[] args) {
        try {
            logger.debug("Start the eProof Sample Program"); //Display the string.

            logger.debug("arg Len=" + args.length); //Display the string.
        /*
        // Quick Debug Mode
        //argsdebug[0] = "TEST";   //""INIT";
        argsdebug[0] = "ISSUE-EPROOF";   //""INIT";
        argsdebug[1] = "C:/eproofsampleprog";
        argsdebug[2] = "issueEproofSample4.json";

        args =argsdebug ;
        FileUtils.deleteDirectory(new File(argsdebug[1])); //Empty existing directory and refresh everytime
         */
            //-------------------------

            if (args.length < 2) {
                URL resource = Run.class.getClassLoader().getResource("help.txt");
                String strPk = readFileAsString(resource.toURI());
                System.out.println(strPk);
            } else {
                mainRunning(args);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mainRunning(String[] args) throws Exception {

        SysObj sysObj = new SysObj();
        if ((args[0]).startsWith("DEBUG-")){
            sysObj.setIDEMode(true);
        }else {
            sysObj.setIDEMode(false);
        }




        // Common
        sysObj.setRootPath(args[1]);
        String initcheckError = SysUtil.init(sysObj);

        if (initcheckError.length() == 0) {

            logger.info("============================================");
            logger.info("Handling Action [" + args[0] + "]");
            logger.info("Root Folder " + args[1]);
            sysObj.setTESTMode(false);
            // By Action
            switch (args[0]) {
                //case "INIT":
                //    logger.info("Finish folder Init in " + args[1]);
                //    break;
                //case "LIST-ALL-EPROOF-TYPE":
                //    Issuer.listEProofTypes(sysObj);
                //    break;
                case "INIT-OR-TEST":
                    Issuer.testconfig(sysObj);   //Debug functions
                    break;
                case "ISSUE-EPROOF-TEST":
                    sysObj.setTESTMode(true);
                    Issuer.issueEProof(sysObj,args[2]);
                    break;
                case "ISSUE-EPROOF":
                    Issuer.issueEProof(sysObj,args[2]);
                    break;
                case "DEBUG-INIT-OR-TEST":
                    Issuer.testconfig(sysObj);   //Debug functions
                    break;
                case "DEBUG-ISSUE-EPROOF":
                    Issuer.issueEProof(sysObj,args[2]);
                    break;
                case "DEBUG-ISSUE-EPROOF-TEST":
                    sysObj.setTESTMode(true);
                    Issuer.issueEProof(sysObj,args[2]);
                    break;
                case "ISSUE-EPROOF-IGS":
                    Issuer.issueEProof2(sysObj,args[2]);
                default:
                    logger.info("Unsupported Action " + args[0]);
                    break;
            }
        }
        if (initcheckError.length() > 0){
            logger.error("Error Found. " + initcheckError); //Display the string.
        }
    }
}
