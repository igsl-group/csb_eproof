-- Create Department
insert into DB_PMS.department (department_id, create_date, modified_date, department_code, department_name, auditor_details_created_by, auditor_details_modified_by)
values  (1 , now(), null, 'CEDB ', 'Commerce and Economic Development Bureau ', null, null),
        (2, now(), null, 'CEO', 'Chief Executive''s Office', null, null),
        (3, now(), null, 'CEPU', 'Chief Executive''s Policy Unit', null, null),
        (4, now(), null, 'CMAB', 'Constitutional and Mainland Affairs Bureau', null, null),
        (5, now(), null, 'CSB', 'Civil Service Bureau', null, null),
        (6, now(), null, 'CSO', 'Chief Secretary for Administration''s Office', null, null),
        (7, now(), null, 'CSPO', 'Chief Secretary for Administration''s Private Office', null, null),
        (8, now(), null, 'CSTB', 'Culture, Sports and Tourism Bureau', null, null),
        (9, now(), null, 'DEVB', 'Development Bureau', null, null),
        (10, now(), null, 'DoJ', 'Department of Justice', null, null),
        (11, now(), null, 'EDB', 'Education Bureau', null, null),
        (12, now(), null, 'EEB', 'Environment and Ecology Bureau', null, null),
        (13, now(), null, 'ENB', 'Environment Bureau', null, null),
        (14, now(), null, 'ExCO', 'Executive Council', null, null),
        (15, now(), null, 'FHB', 'Food and Health Bureau', null, null),
        (16, now(), null, 'FSO', 'Financial Secretary''s Office', null, null),
        (17, now(), null, 'FSTB', 'Financial Services and the Treasury Bureau', null, null),
        (18, now(), null, 'HAB', 'Home Affairs Bureau', null, null),
        (19, now(), null, 'HAD', 'Home Affairs Department', null, null),
        (20, now(), null, 'HB', 'Housing Bureau', null, null),
        (21, now(), null, 'HHB', 'Health Bureau', null, null),
        (22, now(), null, 'HYAB', 'Home and Youth Affairs Bureau', null, null),
        (23, now(), null, 'ISD', 'Information Services Department', null, null),
        (24, now(), null, 'ITIB', 'Innovation, Technology and Industry Bureau', null, null),
        (25, now(), null, 'LWB', 'Labour and Welfare Bureau', null, null),
        (26, now(), null, 'SB', 'Security Bureau', null, null);


-- Create User
insert into DB_PMS.user (user_id, create_date, modified_date, email, last_login_date, name, phone_number, post, status,
                         auditor_details_created_by, auditor_details_modified_by, department_id, login_id)
values (1, now(), null, '', null, 'admin', '', '', 'Active', null, null, 1, 'admin'),
       (2, now(), null, 'user_sss@yopmail.com', null, 'User SSS', '38556910', 'Superior of System Secretariat', 'Active', null, null, 1, 'user_sss'),
       (3, now(), null, 'user_ss@yopmail.com', null, 'User SS', '38556911', 'System Secretariat', 'Active', null, null, 1, 'user_ss'),
       (4, now(), null, 'user_su@yopmail.com', null, 'User SU', '38556912', 'System User', 'Active', null, null, 1, 'user_su');

-- Map User <--> Password
insert into DB_PMS.password (password_id, create_date, modified_date, password_hash, auditor_details_created_by,
                             auditor_details_modified_by, user_id)
values (1, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 1),
       (2, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 2),
       (3, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 3),
       (4, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 4);

insert into DB_PMS.role (role_id, create_date, modified_date, code, name, auditor_details_created_by,
                         auditor_details_modified_by)
values (1, now(), null, 'SA', 'System Administrator', null, null),
       (2, now(), null, 'SSS', 'Superior of System Secretariat', null, null),
       (3, now(), null, 'SS', 'System Secretariat', null, null),
       (4, now(), null, 'SU', 'System User', null, null);

insert into DB_PMS.permission (id, code, name)
values (1, 'USER_MAINTENANCE', 'User Maintenance'),
       (2, 'USER_VIEWER', 'User Viewer'),
       (3, 'ACCESS_CONTROL_MAINTENANCE', 'Access Control Maintenance'),
       (4, 'ACCESS_CONTROL_VIEWER', 'Access Control Viewer'),
       (5, 'MEETING_WORKSPACE_MAINTENANCE', 'Meeting Workspace Maintenance'),
       (6, 'APPROVE', 'Approve'),
       (7, 'BD_TABLE_MAINTENANCE', 'B/D Table Maintenance'),
       (8, 'BD_TABLE_VIEWER', 'B/D Table Viewer'),
       (9, 'GROUP_TABLE_VIEWER', 'Group Table Viewer'),
       (10, 'GROUP_TABLE_MAINTENANCE', 'Group Table Maintenance'),
       (11, 'CONFIGURATION_MAINTENANCE', 'Configuration Maintenance'),
       (12, 'AUDIT_TRAIL_VIEWER', 'Audit Trail Viewer'),
       (13, 'SESSION_CONTROL_MAINTENANCE', 'Session Control Maintenance');

insert into DB_PMS.role_has_permission (permission_id, role_id, create_date, modified_date, auditor_details_created_by,
                                        auditor_details_modified_by)
values (1, 1, now(), null, null, null),
       (2, 1, now(), null, null, null),
       (3, 1, now(), null, null, null),
       (4, 1, now(), null, null, null),
       (5, 1, now(), null, null, null),
       (6, 1, now(), null, null, null),
       (7, 1, now(), null, null, null),
       (8, 1, now(), null, null, null),
       (9, 1, now(), null, null, null),
       (10, 1, now(), null, null, null),
       (11, 1, now(), null, null, null),
       (12, 1, now(), null, null, null),
       (13, 1, now(), null, null, null),
       (1, 2, now(), null, null, null),
       (2, 2, now(), null, null, null),
       (3, 2, now(), null, null, null),
       (4, 2, now(), null, null, null),
       (5, 2, now(), null, null, null),
       (6, 2, now(), null, null, null),
       (7, 2, now(), null, null, null),
       (8, 2, now(), null, null, null),
       (9, 2, now(), null, null, null),
       (10, 2, now(), null, null, null),
       (1, 3, now(), null, null, null),
       (2, 3, now(), null, null, null),
       (3, 3, now(), null, null, null),
       (4, 3, now(), null, null, null),
       (5, 3, now(), null, null, null),
       (7, 3, now(), null, null, null),
       (8, 3, now(), null, null, null),
       (9, 3, now(), null, null, null),
       (10, 3, now(), null, null, null);

-- Map User <--> Role
insert into DB_PMS.user_has_role (role_id, user_id, create_date, modified_date, auditor_details_created_by,
                                  auditor_details_modified_by)
values (1, 1, now(), null, null, null),
       (2, 2, now(), null, null, null),
       (3, 3, now(), null, null, null),
       (4, 4, now(), null, null, null);

INSERT INTO DB_PMS.email_template (create_date, modified_date, body, subject, template_code, auditor_details_created_by,
                                   auditor_details_modified_by)
VALUES (null, null, '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:remove="all" th:text="#{template.title}"></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<span th:text="#{template.reset.password.info}" />
	<br/>
	<a th:href="#{template.reset.password.link(${serverUrl},${token})}"> <span th:text="#{template.reset.password.text}" /> </a>
	<br/><br/>
	<span th:text="#{template.department.info}" />
	<br/><br/>
<p>
    <span th:text="#{template.generated.info}" />
</p>
</body>
</html>', 'PMS Password Reset', 'PasswordReset', null, null),
       (null, null, '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:remove="all" th:text="#{template.title}"></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<span th:text="#{template.password.expire.reminder.info(${passwordExpirationDays})}" />
	<br/><br/>
	<span th:text="#{template.department.info}" />
	<br/><br/>
<p>
    <span th:text="#{template.generated.info}" />
</p>
</body>
</html>', 'PMS Password Expire Reminder', 'PasswordExpiryReminder', null, null);

insert into DB_PMS.configuration (create_date, modified_date, code, label, value,
                                  auditor_details_created_by,
                                  auditor_details_modified_by)
values (now(), null, 'workspace.record.max.no', 'Retain the maximum number of past meeting workspace records', '30',
        null, null),
       (now(), null, 'workspace.days.remaining.no', 'Days remaining before deletion', '60', null, null),
       (now(), null, 'password.expiry.reminder.day', 'Password expiry reminder', '53', null, null),
       (now(), null, 'password.change.min.day', 'Change password at least every N days', '99999', null, null),
       (now(), null, 'password.record.max.no', 'Password history', '10', null, null),
       (now(), null, 'audit.record.max.no', 'Maximum number of audit log records', '200000', null, null),
       (now(), null, 'login.max.attempts', 'Maximum time of login', '5', null, null);



-- Create Groups
insert into DB_PMS.meeting_group (meeting_group_id, create_date, modified_date, code, name, auditor_details_created_by, auditor_details_modified_by)
values  (1, now(), null, 'AD_M', 'AD(M)', null, null),
        (2, now(), null, 'CEC', 'CEC', null, null),
        (4, now(), null, 'CS', 'CS', null, null),
        (5, now(), null, 'D_of_Adm', 'D of Adm', null, null),
        (6, now(), null, 'DECO', 'DECO', null, null),
        (7, now(), null, 'DHA', 'DHA', null, null),
        (8, now(), null, 'DIS', 'DIS', null, null),
        (9, now(), null, 'DPS', 'DPS', null, null),
        (10, now(), null, 'FS', 'FS', null, null),
        (11, now(), null, 'Guest', 'Guest', null, null),
        (12, now(), null, 'H_of_PICO', 'H of PICO', null, null),
        (13, now(), null, 'Perm_Secy_of_CEO', 'Perm Secy of CEO', null, null),
        (14, now(), null, 'PS_of_CE', 'PS of CE', null, null),
        (15, now(), null, 'PSCCI', 'PSCCI', null, null),
        (16, now(), null, 'PSCIT', 'PSCIT', null, null),
        (17, now(), null, 'S_for_IT', 'S for IT', null, null),
        (18, now(), null, 'S_for_S', 'S for S', null, null),
        (19, now(), null, 'SCED', 'SCED', null, null),
        (20, now(), null, 'SCMA', 'SCMA', null, null),
        (21, now(), null, 'SCS', 'SCS', null, null),
        (22, now(), null, 'SCST', 'SCST', null, null),
        (23, now(), null, 'SDEV', 'SDEV', null, null),
        (24, now(), null, 'SED', 'SED', null, null),
        (25, now(), null, 'SEE', 'SEE', null, null),
        (26, now(), null, 'SEN', 'SEN', null, null),
        (27, now(), null, 'SFH', 'SFH', null, null),
        (28, now(), null, 'SFST', 'SFST', null, null),
        (29, now(), null, 'SH', 'SH', null, null),
        (30, now(), null, 'SHA', 'SHA', null, null),
        (31, now(), null, 'SHH', 'SHH', null, null),
        (32, now(), null, 'SHYA', 'SHYA', null, null),
        (33, now(), null, 'SITI', 'SITI', null, null),
        (34, now(), null, 'SJ', 'SJ', null, null),
        (35, now(), null, 'SLW', 'SLW', null, null),
        (36, now(), null, 'SOM_LM', 'SOM LM', null, null),
        (37, now(), null, 'SSA', 'SSA', null, null),
        (38, now(), null, 'STH', 'STH', null, null),
        (39, now(), null, 'STL', 'STL', null, null);

-- Map User <--> Group
insert into DB_PMS.user_has_meeting_group (meeting_group_id, user_id, create_date, modified_date,
                                           auditor_details_created_by, auditor_details_modified_by)
values (1, 1, now(), null, null, null),
       (1, 2, now(), null, null, null),
       (1, 3, now(), null, null, null),
       (1, 4, now(), null, null, null);


