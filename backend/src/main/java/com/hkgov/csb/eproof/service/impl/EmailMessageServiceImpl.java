package com.hkgov.csb.eproof.service.impl;

import com.hkgov.csb.eproof.dao.EmailMessageRepository;
import com.hkgov.csb.eproof.entity.EmailMessage;
import com.hkgov.csb.eproof.service.EmailMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
* @author David
* @description 针对表【email_message】的数据库操作Service实现
* @createDate 2024-09-26 15:59:42
*/
@Service
@RequiredArgsConstructor
public class EmailMessageServiceImpl implements EmailMessageService {
    private final EmailMessageRepository emailMessageRepository;
    @Override
    public Page<EmailMessage> emailList(Pageable pageable, String keyword) {
        return emailMessageRepository.findPage(pageable,keyword);
    }
}
