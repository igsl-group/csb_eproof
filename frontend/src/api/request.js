import request from '@/api/index';
export function loginAPI(params, ...rest) {
    switch (params) {
        case 'authenticate':
            return request(`/token?${rest[0]}`, 'authenticate', rest[1]);
        case 'profile':
            return request('/profile', 'get');
        case 'logout':
            return request('/api/v1/auth/logout', 'post');
        case 'user':
            return request('/api/v1/user/getCurrentUser', 'get');
        case 'forgotPassword':
            return request('/api/v1/user/forgotPassword?email=' + rest[0], 'post');
        case 'resetPassword':
            return request('/api/v1/user/resetPassword?token=' + rest[0], 'post', rest[1]);
        case 'changePassword':
            return request('/api/v1/user/changePassword?loginId=' + rest[0], 'post', rest[1]);
        default:
            return;
    }
};

export function documentAPI(params, ...rest) {
    switch (params) {
        case 'create':
            return request('/documents', 'post', rest[0]);
        case 'create':
            return request('/documents', 'post', rest[0]);
        case 'update':
            return request('/documents', 'put', rest[0]);
        case 'list':
            return request(`/documents?${rest[0]}`, 'get');
        case 'view':
            return request(`/documents/${rest[0]}`, 'get');
        case 'updateDoc':
            return request(`/documents/${rest[0]}/docx`, 'form-data-put', rest[1]);
        case 'doc-submit':
            return request(`/documents/${rest[0]}/status/submit`, 'put', rest[1]);
        case 'doc-withdraw':
            return request(`/documents/${rest[0]}/status/withdraw`, 'put', rest[1]);
        case 'doc-reject':
            return request(`/documents/${rest[0]}/status/reject`, 'put', rest[1]);
        case 'confirm-doc':
            return request(`/documents/${rest[0]}/status/confirm`, 'form-data-put', rest[1]);
        case 'doc-despatch':
            return request(`/documents/${rest[0]}/status/despatch`, 'put', rest[1]);
        case 'view-doc':
            return request(`/documents/${rest[0]}/docx`, 'get');
        case 'view-pdf':
            return request(`/documents/${rest[0]}/pdf`, 'download');
        case 'view-pdf-for-sign':
            return request(`/documents/${rest[0]}/pdf`, 'download');
        case 'upload-pdf':
            return request(`/documents/${rest[0]}/pdf`, 'form-data-put', rest[1]);
        case 'get-pdf-for-eproof-signing':
            return request(`/documents/${rest[0]}/pdf`, 'download-put', rest[1]);
        case 'action':
            return request(`/documents/${rest[0]}/actions`, 'get');
        case 'sign-pdf':
            return request(`/signPdf`, 'sign', rest[0]);
        case 'sign-string':
            return request(`/signString?${rest[0]}`, 'sign-string');
        case 'get-signing-cert':
            return request(`/signingCert`, 'get-signing-cert');
        case 'view-sfdt':
            return request(`/documents/${rest[0]}/sfdt`, 'download');
        case 'document-history':
            return request(`/documents/${rest[0]}/history?${rest[1]}`, 'get');
        case 'attachment-download':
            return request(`/documents/${rest[0]}/attachments/${rest[1]}/file`, 'download');
        case 'attachment-list':
            return request(`/documents/${rest[0]}/attachments?${rest[1]}`, 'get');
        case 'attachment-create':
            return request(`/documents/${rest[0]}/attachments`, 'post', rest[1]);
        case 'attachment-remove':
            return request(`/documents/${rest[0]}/attachments`, 'delete', rest[1]);
        default:
            return;
    }
};

export function caseAPI(params, ...rest) {
    switch (params) {
        case 'create':
            return request('/cases', 'post', rest[0]);
        case 'update':
            return request('/cases', 'put', rest[0]);
        case 'list':
            return request(`/cases?${rest[0]}`, 'get');
        case 'view':
            return request(`/cases/${rest[0]}`, 'get');
        case 'supp-doc-list':
            return request(`/cases/${rest[0]}/supp-docs?${rest[1]}`, 'get');
        case 'supp-doc-create':
            return request(`/cases/${rest[0]}/supp-docs`, 'formData', rest[1]);
        case 'supp-doc-download':
            return request(`/cases/${rest[0]}/supp-docs/${rest[1]}/file`, 'download');
        case 'supp-doc-remove':
            return request(`/cases/${rest[0]}/supp-docs?${rest[1]}`, 'delete');
        case 'bd-list':
            return request(`/lists/bd?${rest[0]}`, 'get');
        case 'section-list':
            return request(`/lists/section?${rest[0]}`, 'get');
        default:
            return;
    }
};

export function templateAPI(params, ...rest) {
    switch (params) {
        case 'template-list':
            return request(`/templates?${rest[0]}`, 'get');
        case 'template-version':
            return request(`/templates/${rest[0]}/versions?${rest[1]}`, 'get');
        case 'template-version-current':
            return request(`/templates/${rest[0]}/versions/current`, 'get');
        default:
            return;
    }
};

export function listAPI(params, ...rest) {
    switch (params) {
        case 'bd-list':
            return request(`/lists/bd?${rest[0]}`, 'get');
        case 'section-list':
            return request(`/lists/section?${rest[0]}`, 'get');
        case 'status-list':
            return request(`/lists/document-status`, 'get');
        default:
            return;
    }
};

export function licenceTypeAPI(params, ...rest) {
    switch (params) {
        case 'licence-type-list':
            return request(`/licence-types?${rest[0]}`, 'get');
        case 'licence-type-update':
            return request(`/licence-types`, 'put');
        case 'licence-type-create':
            return request(`/licence-types`, 'post');
        default:
            return;
    }
};

export function postAPI(params, ...rest) {
    switch (params) {
        case 'post-list':
            return request(`/posts?${rest[0]}`, 'get');
        default:
            return;
    }
};

export function roleAPI(params, ...rest) {
    switch (params) {
        case 'allRoles':
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            if (rest[4]) {
                url = url + "&keyword=" + rest[4];
            }
            return request('/api/v1/role/search?' + url, "get");
        case 'allPermissions':
            return request('/api/v1/permission/dropdown', "get");
        case 'search':
            return request('/api/v1/role/search?keyword=' + rest[0], "get");
        case 'getRole':
            return request('/api/v1/role/get?code=' + rest[0], "get");
        case 'deleteRole':
            return request('/api/v1/role/remove?code=' + rest[0], "patch");
        case 'addRole':
            return request('/api/v1/role/create', "post", rest[0]);
        case 'updateRole':
            return request('/api/v1/role/update', "patch", rest[0]);

    }
}

export function userAPI(params, ...rest) {
    switch (params) {
        case 'getAll':
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            if (rest[4]) {
                url = url + "&keyword=" + rest[4];
            }
            return request('/api/v1/user/search?' + url, "get");
        case "getAllRoles":
            return request('/api/v1/role/dropdown', "get");
        case "getAllGroups":
            return request('/api/v1/meetingGroup/dropdown', "get");
        case 'createUser':
            return request('/api/v1/user/create', "post", rest[0]);
        case 'updateUser':
            return request('/api/v1/user/update', "patch", rest[0]);
        case 'deleteUser':
            return request('/api/v1/user/remove?loginId=' + rest[0], "delete");
        case 'getUser':
            return request('/api/v1/user/get?loginId=' + rest[0], "get");
        case 'search':
            return request('/api/v1/user/search?keyword=' + rest[0], "get");
        case 'getAllOffices':
            return request('/api/v1/department/dropdown', "get");
        case 'batchUser':
            return request('/api/v1/user/batch/create', "formData", rest[0]);
            break;
        default:
            return;
    }
};

export function departmentAPI(params, ...rest) {
    switch (params) {
        case 'getAll':
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            if (rest[4]) {
                url = url + "&keyword=" + rest[4];
            }
            return request('/api/v1/department/search?' + url, "get");
        case 'createDepartment':
            return request('/api/v1/department/create', "post", rest[0]);
        case 'updateDepartment':
            return request('/api/v1/department/update', "patch", rest[0]);
        case 'search':
            return request('/api/v1/department/search?keyword=' + rest[0], "get");
        case 'deleteDepartment':
            return request('/api/v1/department/remove?departmentCode=' + rest[0], "delete");
        case 'getDepartment':
            return request('/api/v1/department/get?code=' + rest[0], "get");
        default:
            return;
    }
}
export function profileAPI(params, ...rest) {
    switch (params) {
        case 'change':
            return request('/api/v1/user/profile', "patch", rest[0]);
        default:
            return;
    }
}

export function fullTextSearchAPI(params, ...rest) {
    switch (params) {
        case 'search':
            return request('/api/v1/meetingWorkspace/search?keyword=' + rest[0], "get");
        default:
            return;
    }
}

export function meetingAPI(params, ...rest) {
    switch (params) {
        case 'getAllMeetings': {
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            return request('/api/v1/meetingWorkspace/getAllMeetingWorkspace?' + url, "get");
        }
        case 'getAllRetentionMeetings': {
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            return request('/api/v1/meetingWorkspace/meetingWorkspaceRetentionList?' + url, "get");
        }
        case 'getAllLocations': {
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            return request('/api/v1/location/getAll?' + url, "get");
        }
        case 'createMeetings':
            return request('/api/v1/meetingWorkspace/create', "post", rest[0]);

        default:
            return;
    }
}
export function meetSomAPI(params, ...rest) {
    switch (params) {
        case 'getMeeting':
            return request('/api/v1/meetingWorkspace/' + rest[0] , "get");
        // agenda
        case 'getAgendaAll':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/agendaItem', "get");
        case 'createAgendaItem':
            return request('/api/v1/agendaItem/create', "post", rest[0]);
        case 'updateAgendaItem':
            return request('/api/v1/agendaItem/update', "patch", rest[0]);
        case 'removeAgendaItem':
            return request('/api/v1/agendaItem/remove?agendaItemId=' + rest[0], "delete");
        case 'approveAgendaItem':
            return request('/api/v1/agendaItem/approve?agendaItemId=' + rest[0], 'patch');
        case 'rejectAgendaItem':
            return request('/api/v1/agendaItem/reject?agendaItemId=' + rest[0], 'patch');
        case 'deleteMeeting':
            return request('/api/v1/meetingWorkspace/remove?meetingWorkspaceId=' + rest[0], "delete");
        // attendee
        case 'getAttendeeAll':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/attendee/getAll', "get");
        case 'dropdown':
            return request('/api/v1/user/dropdown', "get");
        case 'createAttendee':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/attendee/add?loginId=' + `${rest[1]}`, "post");
        case 'batchAttendees':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` +'/attendee/batch/add', "formData", rest[1]);
        case 'removeAttendee':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/attendee/remove', "delete", rest[1]);
        // task
        case 'getTaskAll':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/task', 'get');
        case 'addTask':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/task/add', 'post', rest[1]);
        case 'removeTask':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/task/remove?taskId=' + `${rest[1]}`, "delete");
        case 'uploadDoc':
            return request('/api/v1/document/meetingDocument/' + `${rest[0]}/${rest[1]}` + '/upload', "formData", rest[2]);
        case 'removeDoc':
            return request('/api/v1/document/remove?path=' + `${rest[0]}`, 'delete');
        case 'approveDoc':
            return request('/api/v1/document/meetingDocument/' + `${rest[0]}/${rest[1]}/` + 'approve?path=' + `${rest[2]}`, 'patch');
        case 'rejectDoc':
            return request('/api/v1/document/meetingDocument/' + `${rest[0]}/${rest[1]}/` + 'reject?path=' + `${rest[2]}`, 'patch');
        case 'downloadDoc':
            return request('/api/v1/document/get?path=' + `${rest[0]}`, 'download');
        case 'postList':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/postMeetingDocuments', 'get');
        case 'postUploadDoc':
            return request('/api/v1/document/postMeetingDocument/' + `${rest[0]}` + '/upload', "formData", rest[1]);
        case 'postRemoveDoc':
            return request('/api/v1/document/postMeetingDocument/'  + `${rest[0]}` +  '/remove?path=' + `${rest[1]}`, 'delete');
        case 'postApproveDoc':
            return request('/api/v1/document/postMeetingDocument/' + `${rest[0]}` + '/approve?path=' + `${rest[1]}`, 'patch');
        case 'postRejectDoc':
            return request('/api/v1/document/postMeetingDocument/' + `${rest[0]}` + '/reject?path=' + `${rest[1]}`, 'patch');
        case 'privateList':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/privateMeetingDocuments', 'get');
        case 'privateUpload':
            return request('/api/v1/document/privateDocument/' + `${rest[0]}` + '/' + rest[1] + '/upload', 'formData', rest[2]);
        case 'privateRemove':
            return request('/api/v1/document/privateDocument/remove?path=' + `${rest[0]}`, 'delete');
        case 'privateCreateFolder':
            return request('/api/v1/document/privateDocument/' + `${rest[0]}` + '/' + `${rest[1]}` + '/createFolder?path=' + rest[2], 'post');
        case 'freeze':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/freeze', 'post');
        case 'unfreeze':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/unfreeze', 'post');
        case 'copyDoc':
            return request('/api/v1/document/privateDocument/' + `${rest[0]}` + '/' + `${rest[1]}` + '/copyDocument?sourcePath=' + `${rest[2]}` + '&targetPath=' + `${rest[3]}`, 'post');
        case 'editTask':
            return request('/api/v1/meetingWorkspace/' + `${rest[0]}` + '/task/edit', 'post', rest[1]);
        case 'editTitle':
            return request('/api/v1/meetingWorkspace/edit', 'post', rest[0]);
        case 'getAllLocations': {
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            return request('/api/v1/location/getAll?' + url, "get");
        }
        default:
            return;
    }
}

export function calendarAPI(params, ...rest) {
    switch (params) {
        case 'calendar':
            let url = "?year=" + rest[0]
            if (rest[1]) {
                url = url + "&month=" + rest[1];
            }
            return request('/api/v1/meetingWorkspace/calendar' + url, "get");
        default:
            return;
    }
}

export function configurationAPI(params, ...rest) {
    switch (params) {
        case 'all':
            return request('/api/v1/configuration/all', 'get');
        case 'update':
            return request('/api/v1/configuration/', 'patch', rest[0]);
        default:
            return;
    }
}

export function auditAPI(params, ...rest) {
    switch (params) {
        case 'export':
            return request(`/api/v1/auditTrail/csv?from=${rest[0]}&to=${rest[1]}`, 'download');
        case 'logs':
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            if (rest[4]) {
                url = url + "&keyword=" + rest[4];
            }
            return request('/api/v1/auditTrail/search?' + url, "get");
    }
}

export function meetingGroupAPI(params, ...rest) {
    switch (params) {
        case 'getAll':
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            if (rest[4]) {
                url = url + "&keyword=" + rest[4];
            }
            return request('/api/v1/meetingGroup/search?' + url, "get");
        case 'search':
            return request('/api/v1/meetingGroup/search?keyword=' + rest[0], "get");
        case 'createMeetingGroup':
            return request('/api/v1/meetingGroup/create', "post", rest[0]);
        case 'updateMeetingGroup':
            return request('/api/v1/meetingGroup/update', "patch", rest[0]);
        case 'deleteMeetingGroup':
            return request('/api/v1/meetingGroup/remove?code=' + rest[0], "patch");
        case 'getMeetingGroup':
            return request('/api/v1/meetingGroup/get?code=' + rest[0], "get");
        default:
            return;
    }
}

export function onlineUserAPI(params, ...rest) {
    switch (params) {
        case 'getAll':
            let url = "page=" + rest[0] + "&size=" + rest[1];
            if (rest[2]) {
                url = url + "&direction=" + rest[2];
            }
            if (rest[3]) {
                url = url + "&properties=" + rest[3];
            }
            if (rest[4]) {
                url = url + "&keyword=" + rest[4];
            }
            return request('/api/v1/userSession/search?' + url, "get");
        case 'sessionAlive':
            return request('/api/v1/userSession', "patch");
        case 'lockoutOther':
            return request('/api/v1/userSession?sessionId=' + rest[0], "delete");
        case 'logout':
            return request('/api/v1/auth/logout', "post")
        case 'heartAPI':
            return request('/api/v1/userSession', "patch")

    }
}
