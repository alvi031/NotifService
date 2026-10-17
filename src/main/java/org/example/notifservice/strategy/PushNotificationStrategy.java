package org.example.notifservice.strategy;


import lombok.extern.slf4j.Slf4j;
import org.example.notifservice.database.entity.NotificationPreference;
import org.example.notifservice.event.TaskEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public void send(TaskEvent event, NotificationPreference preference) {
        log.info("Push notification: destination={}, eventType={}, taskTitle={}",
                preference.getDestination(),
                event.eventType(),
                event.title()
        );
    }
}