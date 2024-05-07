package com.hkgov.csb.eproof.audit.common;

import com.hkgov.csb.eproof.audit.common.web.ClientInfo;
import com.hkgov.csb.eproof.audit.common.web.ClientInfoHolder;
import com.hkgov.csb.eproof.dao.AuditorDetailsRepository;
import com.hkgov.csb.eproof.entity.AuditorDetails;
import com.hkgov.csb.eproof.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.hkgov.csb.eproof.config.Constants.ANONYMOUS_USER;
import static com.hkgov.csb.eproof.config.Constants.ANONYMOUS_USER_NAME;

public class AuditorAwareImpl implements AuditorAware<AuditorDetails> {

    private final AuditorDetailsRepository auditorDetailsRepository;
    private final EntityManager entityManager;

    public AuditorAwareImpl(AuditorDetailsRepository auditorDetailsRepository, EntityManager entityManager) {
        this.auditorDetailsRepository = auditorDetailsRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<AuditorDetails> getCurrentAuditor() {
        entityManager.setFlushMode(FlushModeType.COMMIT);
        String userId = getUserId();
        String userName = getUserName();
        String post = getPost();
        String hostName = getHostName();
        AuditorDetails auditor = getAuditorDetails(userId, userName, post, hostName);
        entityManager.setFlushMode(FlushModeType.AUTO);
        return Optional.of(auditor == null ? createAuditorDetails(userId, userName, post, hostName) : auditor);
    }

    private AuditorDetails createAuditorDetails(String userId, String userName, String post, String hostName) {
        AuditorDetails auditorDetails = new AuditorDetails();
        auditorDetails.setUserId(userId);
        auditorDetails.setUserName(userName);
        auditorDetails.setPost(post);
        auditorDetails.setHostname(hostName);
        return auditorDetailsRepository.save(auditorDetails);
    }

    private AuditorDetails getAuditorDetails(String userId, String userName, String post, String hostName) {
        return auditorDetailsRepository.findByUserIdAndUserNameAAndHostAndHostname(userId, userName, post, hostName);
    }

    private static String getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String p) {
            return p.equals(ANONYMOUS_USER) ? ANONYMOUS_USER_NAME : p;
        } else if (principal instanceof User u) {
            return u.getLoginId();
        }
        return ANONYMOUS_USER_NAME;
    }

    private static String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String p) {
            return p.equals(ANONYMOUS_USER) ? ANONYMOUS_USER_NAME : p;
        } else if (principal instanceof User u) {
            return u.getName();
        }
        return ANONYMOUS_USER_NAME;
    }

    private String getHostName() {
        ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
        if (clientInfo != null) {
            return clientInfo.getClientIpAddress();
        }
        return null;
    }

    private String getPost() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String p) {
            return p.equals(ANONYMOUS_USER) ? ANONYMOUS_USER_NAME : p;
        } else if (principal instanceof User u) {
            return u.getPost();
        }
        return null;
    }
}
