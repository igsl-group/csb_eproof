package com.hkgov.csb.eproof.constants;

public final class Constants {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_PATTERN_2 = "dd MMMM yyyy";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_PATTERN_2 = "yyyy-MM-dd-HH-mm-ss";
    public static final String COOKIE_KEY_LOGIN_UID = "uid";
    public static final String COOKIE_KEY_LOGIN_DPDEPTID = "dpdeptid";
    public static final String EXAM_DATE = "MM/dd/yyyy";


    public static final String STATUS_ACTIVE = "ACTIVE";

    public static final String EMAIL_STATUS_COMPLETED = "COMPLETED";
    public static final String EMAIL_STATUS_PENDING = "PENDING";
    public static final String EMAIL_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String EMAIL_STATUS_FAILED = "FAILED";



    public static final String JWT_KEY_USERNAME = "uname";
    public static final String JWT_KEY_USERID = "uid";
    public static final String JWT_KEY_DPUSERID = "dpuserid";
    public static final String JWT_KEY_SESSIONID= "sid";
    public static final String COOKIE_KEY_ACCESS_TOKEN = "access_token";


    public static final String LETTER_TEMPLATE_AT_LEAST_ONE_PASS = "AT_LEAST_ONE_PASS_TEMPLATE";
    public static final String LETTER_TEMPLATE_ALL_FAILED_TEMPLATE = "ALL_FAILED_TEMPLATE";
    public static final String FILE_TYPE_CERT_RECORD = "CERT_RECORD";
    public static final String FILE_TYPE_CERT_RECORD_RENEW = "CERT_RECORD_RENEW";


    public static final String SYS_PARAM_HEALTH_CHECK_MAIL_TEMPLATE = "HEALTH_CHECK_MAIL_TEMPLATE";
    public static final String SYS_PARAM_NOTI_BATCH_XML_LOCATION = "NOTI_BATCH_XML_LOCATION";
    public static final String EMAIL_TEMPLATE_NOTIFY = "NOTIFY";



    private Constants() {
    }
}
