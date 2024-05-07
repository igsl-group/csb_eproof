package com.hkgov.csb.eproof.scheduling;

import com.hkgov.csb.eproof.dao.AuditorDetailsRepository;
import com.hkgov.csb.eproof.service.AuditLogService;
import com.hkgov.csb.eproof.service.ConfigurationService;
import com.hkgov.csb.eproof.service.MeetingWorkspaceService;
import com.hkgov.csb.eproof.service.UserService;
import com.hkgov.csb.eproof.service.UserSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.hkgov.csb.eproof.config.Constants.TASK_SCHEDULER;

@Component
public class ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    private final MeetingWorkspaceService meetingWorkspaceService;
    private final ConfigurationService configurationService;
    private final AuditorDetailsRepository auditorDetailsRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final UserSessionService userSessionService;

    @Value("${cronjob.meetingWorkspace.retention.limit}")
    private int meetingWorkspaceRetentionLimitNo;

    @Value("${cronjob.audit.retention.days}")
    private int auditRetentionDays;

    public ScheduledTask(MeetingWorkspaceService meetingWorkspaceService,
                         ConfigurationService configurationService,
                         AuditorDetailsRepository auditorDetailsRepository,
                         UserService userService,
                         AuditLogService auditLogService,
                         UserSessionService userSessionService) {
        this.meetingWorkspaceService = meetingWorkspaceService;
        this.configurationService = configurationService;
        this.auditorDetailsRepository = auditorDetailsRepository;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.userSessionService = userSessionService;
    }

    @Scheduled(cron = "${cronjob.audit.retention.cronExpression}")
    @Transactional
    public void purgeAuditLog() {
        logger.info("[purgeAuditLog] Cronjob START");
        setTaskScheduler();
        int purgedByRetentionDays = auditLogService.purgeAuditLogByRetentionDays(auditRetentionDays);
        logger.info("[purgeAuditLog] purged {} record(s) that exist(s) more than {} days", purgedByRetentionDays, auditRetentionDays);
        int purgedByMaxNo = auditLogService.purgeAuditLogByMaxNo(configurationService.getAuditLogMaxNo());
        logger.info("[purgeAuditLog] purged {} record(s) that exceed(s) {} maximum number of records", purgedByMaxNo, configurationService.getAuditLogMaxNo());

    }


    @Scheduled(cron = "${cronjob.meetingWorkspace.purge.cronExpression}")
    @Transactional
    public void purgeMeetingWorkspace() {
        logger.info("[purgeMeetingWorkspace] Cronjob START");
        setTaskScheduler();
        int workspaceRetentionDays = configurationService.getWorkspaceRetentionDays();
        meetingWorkspaceService.purgeMeetingWorkspaceByRetention(workspaceRetentionDays);
    }

    @Scheduled(cron = "${cronjob.meetingWorkspace.retention.cronExpression}")
    @Transactional
    public void retainMeetingWorkspaceByLimit() {
        logger.info("[retainMeetingWorkspaceByLimit] Cronjob START");
        logger.info("Add oldest completed meeting workspace to retention pool after {} of records", meetingWorkspaceRetentionLimitNo);
        setTaskScheduler();
        meetingWorkspaceService.retainMeetingWorkspaceByLimit(meetingWorkspaceRetentionLimitNo);
    }

    @Scheduled(cron = "${cronjob.user.password.expire.reminder.cronExpression}")
    @Transactional
    public void userPasswordExpireReminder() {
        logger.info("[userPasswordExpireReminder] Cronjob START");
        setTaskScheduler();
        userService.sendPasswordExpireEmail();
    }

    @Scheduled(cron = "${cronjob.user.session.cleanup.cronExpression}")
    @Transactional
    public void idleUserSessionsCleanup() {
        logger.info("[idleUserSessionsCleanup] Cronjob START");
        setTaskScheduler();
        userSessionService.cleanupIdleUserSessions();
    }

    private void setTaskScheduler() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(auditorDetailsRepository.findByUserId(TASK_SCHEDULER), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
