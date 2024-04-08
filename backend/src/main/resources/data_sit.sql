-- SIT Extra Testing Data

-- Create Groups
insert into DB_PMS.meeting_group (meeting_group_id, create_date, modified_date, code, name, auditor_details_created_by,
                                  auditor_details_modified_by)
values (1, now(), null, 'IGSL_SA', 'IGSL_SA', null, null),
       (2, now(), null, 'IGSL_PM', 'IGSL_PM', null, null),
       (3, now(), null, 'CEO_ITMU', 'CEO_ITMU', null, null);

-- Create Department
insert into DB_PMS.department (department_id, create_date, modified_date, department_code, department_name,
                               auditor_details_created_by, auditor_details_modified_by)
values (2, now(), null, 'IGSL', 'Integrated Global Solutions Limited', null, null);

-- Create User
insert into DB_PMS.user (user_id, create_date, modified_date, email, last_login_date, name, phone_number, post, status,
                         auditor_details_created_by, auditor_details_modified_by, department_id, login_id)
values (2, now(), null, 'user1@yopmail.com', null, 'user1', '38556910', 'Post', 'Active', null, null, 2, 'user1'),
       (3, now(), null, 'wilfred.lai@yopmail.com', null, 'Wilfred Lai', '', '', 'Active', null, null, 2, 'wilfred.lai'),
       (4, now(), null, 'kenny.siu@yopmail.com', null, 'Kenny Siu', '', '', 'Active', null, null, 2, 'kenny.siu'),
       (5, now(), null, 'daniel.ho@yopmail.com', null, 'Daniel Ho', '', '', 'Active', null, null, 2, 'daniel.ho'),
       (6, now(), null, 'eunice@yopmail.com', null, 'Eunice Choi', '', '', 'Active', null, null, 2, 'eunice.choi'),
       (7, now(), null, 'vincent_lau@yopmail.com', null, 'Vincent Lau', '', '', 'Disabled', null, null, 1,
        'vincent.lau');

-- Map User <--> Password
insert into DB_PMS.password (password_id, create_date, modified_date, password_hash, auditor_details_created_by,
                             auditor_details_modified_by, user_id)
values (2, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 2),
       (3, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 3),
       (4, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 4),
       (5, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 5),
       (6, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 6),
       (7, now(), null, '$2a$10$HsV/I2GeMHWCJIb5K4nUj.FRIYVNrGeGOzaKCtkLUxteKuInWdPvW', null, null, 7);

-- Map User <--> Role
insert into DB_PMS.user_has_role (role_id, user_id, create_date, modified_date, auditor_details_created_by,
                                  auditor_details_modified_by)
values (4, 2, now(), null, null, null),
       (1, 3, now(), null, null, null);

-- Map User <--> Group
insert into DB_PMS.user_has_meeting_group (meeting_group_id, user_id, create_date, modified_date,
                                           auditor_details_created_by, auditor_details_modified_by)
values (1, 3, now(), null, null, null),
       (1, 4, now(), null, null, null),
       (1, 5, now(), null, null, null),
       (2, 5, now(), null, null, null),
       (2, 6, now(), null, null, null),
       (3, 7, now(), null, null, null);
