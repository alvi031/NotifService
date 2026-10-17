package org.example.notifservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notifservice.event.TaskEvent;
import org.example.notifservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "task-events")
    public void consume(TaskEvent event) {
        log.info("Received task event: eventId={}, eventType={}",
                event.eventId(),
                event.eventType()
        );

        notificationService.process(event);
    }
}