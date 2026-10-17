package org.example.notifservice.strategy;

import org.example.notifservice.database.entity.NotificationPreference;
import org.example.notifservice.event.TaskEvent;

public interface NotificationStrategy {

    NotificationChannel getChannel();

    void send(TaskEvent event, NotificationPreference preference);
}