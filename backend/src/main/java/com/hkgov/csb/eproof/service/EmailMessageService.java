package com.hkgov.csb.eproof.service;

import com.hkgov.csb.eproof.entity.EmailMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
* @author David
* @description 针对表【email_message】的数据库操作Service
* @createDate 2024-09-26 15:59:42
*/
public interface EmailMessageService {
    Page<EmailMessage> emailList(Pageable pageable, String keyword);
}
