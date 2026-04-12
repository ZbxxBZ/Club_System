package com.bistu.clubsystembackend.config;

import com.bistu.clubsystembackend.service.ScheduledTaskService;
import com.bistu.clubsystembackend.util.ScheduledTaskCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserLifecycleScheduler {

    private final ScheduledTaskService scheduledTaskService;

    public UserLifecycleScheduler(ScheduledTaskService scheduledTaskService) {
        this.scheduledTaskService = scheduledTaskService;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void autoExitGraduatedStudents() {
        scheduledTaskService.executeBySystem(ScheduledTaskCode.GRADUATION_EXIT_CLUB);
    }

    @Scheduled(cron = "0 30 2 * * ?")
    public void autoFreezeCanceledClubAdmins() {
        scheduledTaskService.executeBySystem(ScheduledTaskCode.CLUB_CANCEL_FREEZE_ACCOUNT);
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void autoEndEvents() {
        scheduledTaskService.executeBySystem(ScheduledTaskCode.AUTO_END_EVENTS);
    }
}
