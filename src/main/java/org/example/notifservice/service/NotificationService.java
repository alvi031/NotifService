package org.example.notifservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notifservice.database.entity.NotificationPreference;
import org.example.notifservice.database.repository.NotificationPreferenceRepository;
import org.example.notifservice.event.TaskEvent;
import org.example.notifservice.event.TaskEventType;
import org.example.notifservice.strategy.NotificationChannel;
import org.example.notifservice.strategy.NotificationStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final List<NotificationStrategy> strategies;
    private final NotificationPreferenceRepository preferenceRepository;

    public void process(TaskEvent event) {
        if (event.eventType() != TaskEventType.TASK_CREATED
                && event.eventType() != TaskEventType.TASK_COMPLETED) {
            log.warn("Unsupported event type: {}", event.eventType());
            return;
        }

        List<NotificationPreference> preferences =
                preferenceRepository.findAllByUserIdAndEnabledTrue(
                        event.userId()
                );

        Map<NotificationChannel, NotificationPreference> enabledChannels =
                preferences.stream()
                        .collect(Collectors.toMap(
                                NotificationPreference::getChannel,
                                Function.identity()
                        ));

        for (NotificationStrategy strategy : strategies) {
            NotificationPreference preference =
                    enabledChannels.get(strategy.getChannel());

            if (preference == null) {
                continue;
            }

            try {
                strategy.send(event, preference);
            } catch (Exception exception) {
                log.error(
                        "Failed to send notification via {} for event {}",
                        strategy.getChannel(),
                        event.eventId(),
                        exception
                );
            }
        }
    }
}