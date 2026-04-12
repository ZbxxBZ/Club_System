package com.bistu.clubsystembackend.config;

import com.bistu.clubsystembackend.mapper.UserPermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class EventStatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventStatusScheduler.class);

    private final UserPermissionMapper mapper;

    public EventStatusScheduler(UserPermissionMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 应用启动后立即执行一次
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("应用启动完成，立即执行一次活动状态自动更新任务");
        executeAutoUpdateEventStatus();
    }

    /**
     * 活动状态自动更新定时任务（每分钟执行一次）
     * 1. 报名中(3) → 进行中(6)：活动开始时间已到
     * 2. 进行中(6) → 已结束(4)：活动结束时间已到
     */
    @Scheduled(cron = "0 * * * * ?")
    public void autoUpdateEventStatusScheduled() {
        executeAutoUpdateEventStatus();
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeAutoUpdateEventStatus() {
        try {
            LocalDateTime now = LocalDateTime.now();
            log.info("开始执行活动状态自动更新任务，当前时间: {}", now);
            int startedCount = mapper.autoStartEvents(now);
            log.info("自动开始活动：更新了 {} 个活动状态为进行中", startedCount);
            int endedCount = mapper.autoEndEvents(now);
            log.info("自动结束活动：更新了 {} 个活动状态为已结束", endedCount);
        } catch (Exception e) {
            log.error("活动状态自动更新任务执行失败", e);
        }
    }
}
