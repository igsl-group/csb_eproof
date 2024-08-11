package com.hkgov.csb.eproof.exception;

public class ExceptionConstants {

    private ExceptionConstants() {
    }


    // CODE
    public static final String CLIENT_INFO_CREATION_EXCEPTION_CODE = "client.info.creation.exception";
    public static final String JWT_TOKEN_EXPIRY_EXCEPTION_CODE = "jwt.token.expiry.exception";
    public static final String JWT_TOKEN_INVALID_EXCEPTION_CODE = "jwt.token.invalid.exception";
    public static final String JWT_TOKEN_REPLACED_EXCEPTION_CODE = "jwt.token.replaced.exception";
    public static final String ROLE_NOT_FOUND_EXCEPTION_CODE = "role.not.found.exception";
    public static final String PERMISSION_NOT_FOUND_EXCEPTION_CODE = "permission.not.found.exception";
    public static final String ROLE_HAS_PERMISSION_NOT_FOUND_EXCEPTION_CODE = "roleHasPermission.not.found.exception";
    public static final String DEPARTMENT_NOT_FOUND_EXCEPTION_CODE = "department.not.found.exception";
    public static final String MEETING_WORKSPACE_NOT_FOUND_EXCEPTION_CODE = "meeting.workspace.not.found.exception";
    public static final String AGENDA_ITEM_NOT_FOUND_EXCEPTION_CODE = "agenda.item.not.found.exception";
    public static final String EMAIL_TO_LIST_INVALID_EXCEPTION_CODE = "email.to.list.invalid.exception";
    public static final String READ_VALUE_EXCEPTION_CODE = "read.value.exception";
    public static final String EMAIL_CONTEXT_CONVERT_EXCEPTION_CODE = "email.context.convert.exception";
    public static final String USER_ID_NOT_FOUND_EXCEPTION_CODE = "user.id.not.found.exception";
    public static final String USER_EMAIL_NOT_FOUND_EXCEPTION_CODE = "user.email.not.found.exception";
    public static final String RESET_PASSWORD_TOKEN_INVALID_EXCEPTION_CODE = "reset.password.token.invalid.exception";
    public static final String FAILED_TO_READ_FILE_TYPE_EXCEPTION_CODE = "failed.to.read.file.type.exception";
    public static final String FILE_NAME_ALREADY_EXIST_EXCEPTION_CODE = "file.name.already.exist.exception";
    public static final String FAILED_TO_UPLOAD_FILE_EXCEPTION_CODE = "failed.to.upload.file.exception";
    public static final String FAILED_TO_GET_FILE_EXCEPTION_CODE = "failed.to.get.file.exception";
    public static final String FAILED_TO_REMOVE_FILE_EXCEPTION_CODE = "failed.to.remove.file.exception";
    public static final String GROUP_NOT_FOUND_EXCEPTION_CODE = "group.not.found.exception";
    public static final String MEETING_WORKSPACE_FREEZE_EXCEPTION_CODE = "meeting.workspace.freeze.exception";
    public static final String DUPLICATE_LOGIN_ID_EXCEPTION_CODE = "duplicate.login.id.exception";
    public static final String DUPLICATE_EMAIL_EXCEPTION_CODE = "duplicate.email.exception";

    public static final String USER_SESSION_NOT_FOUND_EXCEPTION_CODE = "user.session.not.found";
    public static final String ACCOUNT_BLOCKED_EXCEPTION_CODE = "account.blocked.exception";
    public static final String ACCOUNT_DISABLED_EXCEPTION_CODE = "account.disabled.exception";
    public static final String FAILED_TO_READ_CSV_EXCEPTION_CODE = "failed.to.read.csv.exception";
    public static final String USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_CODE = "user.name.or.password.incorrect.exception";
    public static final String OLD_PASSWORD_NOT_VALID_EXCEPTION_CODE = "old.password.not.valid";
    public static final String FILE_NOT_FOUND_EXCEPTION_CODE = "file.not.found.exception";
    public static final String PASSWORD_USED_BEFORE_EXCEPTION_CODE = "password.used.before.exception";
    public static final String PASSWORD_EXPIRE_EXCEPTION_CODE = "password.expire.exception";

    public static final String DUPLICATE_MEETING_TITLE_EXCEPTION_CODE = "duplicate.meeting.title.exception";
    public static final String DUPLICATE_MEETING_TIMESLOT_EXCEPTION_CODE = "duplicate.meeting.timeslot.exception";
    public static final String FAILED_TO_READ_TXT_EXCEPTION_CODE = "failed.to.read.txt.exception";
    public static final String EDIT_OWN_ROLE_EXCEPTION_CODE = "edit.own.role.exception";
    public static final String CANNOT_MODIFY_ADMIN_EXCEPTION_CODE = "cannot.modify.admin.exception";
    public static final String USER_CANNOT_DELETE_ITSELF_EXCEPTION_CODE = "user.cannot.delete.itself.exception";
    public static final String USER_CANNOT_EDIT_SELF_STATUS_EXCEPTION_CODE = "user.cannot.edit.self.status.exception";
    public static final String FAILED_TO_CONVERT_CSV_EXCEPTION_CODE = "failed.to.convert.csv.exception";
    public static final String NO_PERMISSION_TO_DOWNLOAD_EXCEPTION_CODE = "no.permission.to.download.exception";

    // MESSAGE
    public static final String CLIENT_INFO_CREATION_EXCEPTION_MESSAGE = "This client info cannot create.";
    public static final String JWT_TOKEN_EXPIRY_EXCEPTION_MESSAGE = "Session is expiry.";
    public static final String JWT_TOKEN_INVALID_EXCEPTION_MESSAGE = "This token is invalid.";
    public static final String JWT_TOKEN_REPLACED_EXCEPTION_MESSAGE = "You have been logged out because there is another login request.";
    public static final String ROLE_NOT_FOUND_EXCEPTION_MESSAGE = "This role does not exist.";
    public static final String PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE = "This permission does not exist.";
    public static final String ROLE_HAS_PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE = "This roleHasPermission does not exist.";
    public static final String DEPARTMENT_HAS_PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE = "This department does not exist.";
    public static final String MEETING_WORKSPACE_NOT_FOUND_EXCEPTION_MESSAGE = "This meeting workspace does not exist.";
    public static final String AGENDA_ITEM_NOT_FOUND_EXCEPTION_MESSAGE = "This agenda item does not exist.";
    public static final String EMAIL_TO_LIST_INVALID_EXCEPTION_MESSAGE = "This email send list invalid.";
    public static final String READ_VALUE_EXCEPTION_MESSAGE = "This value cannot read.";
    public static final String EMAIL_CONTEXT_CONVERT_EXCEPTION_MESSAGE = "This email context cannot convert.";
    public static final String USER_ID_NOT_FOUND_EXCEPTION_MESSAGE = "This user ID cannot find related user.";
    public static final String USER_EMAIL_NOT_FOUND_EXCEPTION_MESSAGE = "This email cannot find related user.";
    public static final String RESET_PASSWORD_TOKEN_INVALID_EXCEPTION_MESSAGE = "This reset password token is invalid.";
    public static final String FAILED_TO_READ_FILE_TYPE_EXCEPTION_MESSAGE = "This file type cannot read.";
    public static final String FILE_NAME_ALREADY_EXIST_EXCEPTION_MESSAGE = "This file name already exist.";
    public static final String FAILED_TO_UPLOAD_FILE_EXCEPTION_MESSAGE = "This file cannot upload.";
    public static final String FAILED_TO_GET_FILE_EXCEPTION_MESSAGE = "This file cannot get.";
    public static final String FAILED_TO_REMOVE_FILE_EXCEPTION_MESSAGE = "This file cannot remove.";
    public static final String GROUP_NOT_FOUND_EXCEPTION_MESSAGE = "This group does not exist.";
    public static final String USER_SESSION_NOT_FOUND_EXCEPTION_MESSAGE = "User session not found";
    public static final String ACCOUNT_BLOCKED_EXCEPTION_MESSAGE = "Your account has locked due to several attempts, please contact System Administrator to unlock.";
    public static final String ACCOUNT_DISABLED_EXCEPTION_MESSAGE = "The server could not sign you in. Make sure your user name and password are correct, and then try again.";
    public static final String MEETING_WORKSPACE_FREEZE_EXCEPTION_MESSAGE = "This meeting workspace is freeze.";
    public static final String FAILED_TO_READ_CSV_EXCEPTION_MESSAGE = "Failed to read csv file";
    public static final String USER_NAME_OR_PASSWORD_INCORRECT_EXCEPTION_MESSAGE = "The server could not sign you in. Make sure your user name and password are correct, and then try again.";
    public static final String OLD_PASSWORD_NOT_VALID_EXCEPTION_MESSAGE = "Old password is not valid";
    public static final String FILE_NOT_FOUND_EXCEPTION_MESSAGE = "File not found";
    public static final String PASSWORD_USED_BEFORE_EXCEPTION_MESSAGE = "Password used before";
    public static final String PASSWORD_EXPIRE_EXCEPTION_MESSAGE = "Password expire";
    public static final String DUPLICATE_MEETING_TITLE_EXCEPTION_MESSAGE = "Duplicate meeting title";
    public static final String DUPLICATE_MEETING_TIMESLOT_EXCEPTION_MESSAGE = "Duplicate meeting timeslot";
    public static final String FAILED_TO_READ_TXT_EXCEPTION_MESSAGE = "Failed to read txt file";
    public static final String EDIT_OWN_ROLE_EXCEPTION_MESSAGE = "Edit your own role is not allowed";
    public static final String CANNOT_MODIFY_ADMIN_EXCEPTION_MESSAGE = "Modify Administrator role is not allowed";
    public static final String USER_CANNOT_DELETE_ITSELF_EXCEPTION_MESSAGE = "Delete your own account is not allowed";
    public static final String USER_CANNOT_EDIT_SELF_STATUS_EXCEPTION_MESSAGE = "Edit your own account status is not allowed";
    public static final String DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE = "Duplicate login name";
    public static final String DUPLICATE_EMAIL_EXCEPTION_MESSAGE = "Duplicate email";
    public static final String FAILED_TO_CONVERT_CSV_EXCEPTION_MESSAGE = "Failed to convert CSV file";
    public static final String NO_PERMISSION_TO_DOWNLOAD_EXCEPTION_MESSAGE = "No permission to download";

    public static final String SERIAL_HAS_EXITED = "examProfile.serial.exits";
        public static final String NOT_ALLOW_TO_RESET_EXAM_PROFILE = "Not allow to reset exam profile.";

    public static final String SERIAL_NOT_EXITED = "serial.not.exits";
}
