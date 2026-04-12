package com.bistu.clubsystembackend.service;

import com.bistu.clubsystembackend.entity.request.ScheduledTaskCommandRequest;
import com.bistu.clubsystembackend.entity.response.ScheduledTaskCommandData;
import com.bistu.clubsystembackend.entity.response.ScheduledTaskStatusData;

import java.util.List;

public interface ScheduledTaskService {
    ScheduledTaskCommandData commandTask(String taskCode, ScheduledTaskCommandRequest request);

    List<ScheduledTaskStatusData> queryStatus(List<String> taskCodes);

    void executeBySystem(String taskCode);
}
