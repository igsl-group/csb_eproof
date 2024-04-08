package com.hkgov.ceo.pms.config;

public class ConfigurationConstants {

    public static final String LOGIN_MAX_ATTEMPTS = "login.max.attempts";
    public static final String WORKSPACE_DAYS_REMAINING_NO = "workspace.days.remaining.no";
    public static final String PASSWORD_CHANGE_MIN_DAY = "password.change.min.day";
    public static final String PASSWORD_RECORD_MAX_NO = "password.record.max.no";
    public static final String PASSWORD_EXPIRY_REMINDER_DAY = "password.expiry.reminder.day";
    public static final String AUDIT_RECORD_MAX_NO = "audit.record.max.no";
    public static final int WORKSPACE_DAYS_REMAINING_NO_DEFAULT_VALUE = 30;
    public static final int AUDIT_RECORD_MAX_NO_DEFAULT_VALUE = 10000;
    public static final int PASSWORD_EXPIRY_REMINDER_DAY_DEFAULT_VALUE = 53;
    public static final int PASSWORD_RECORD_MAX_NO_DEFAULT_VALUE = 5;
    public static final int PASSWORD_CHANGE_MIN_DAY_DEFAULT_VALUE = 60;

    private ConfigurationConstants() {
    }
}
