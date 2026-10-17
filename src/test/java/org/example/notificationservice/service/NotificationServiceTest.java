package org.example.notificationservice.service;

import org.example.notifservice.database.entity.NotificationPreference;
import org.example.notifservice.database.repository.NotificationPreferenceRepository;
import org.example.notifservice.event.TaskEvent;
import org.example.notifservice.event.TaskEventType;
import org.example.notifservice.service.NotificationService;
import org.example.notifservice.strategy.EmailNotificationStrategy;
import org.example.notifservice.strategy.NotificationChannel;
import org.example.notifservice.strategy.PushNotificationStrategy;
import org.example.notifservice.strategy.TelegramNotificationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @Mock
    private EmailNotificationStrategy emailStrategy;

    @Mock
    private TelegramNotificationStrategy telegramStrategy;

    @Mock
    private PushNotificationStrategy pushStrategy;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                List.of(emailStrategy, telegramStrategy, pushStrategy),
                preferenceRepository
        );

        when(emailStrategy.getChannel()).thenReturn(NotificationChannel.EMAIL);
        when(telegramStrategy.getChannel()).thenReturn(NotificationChannel.TELEGRAM);
        when(pushStrategy.getChannel()).thenReturn(NotificationChannel.PUSH);
    }

    @Test
    void shouldSendNotificationsOnlyThroughEnabledChannels() {
        TaskEvent event = createEvent();

        when(preferenceRepository.findAllByUserIdAndEnabledTrue(7L))
                .thenReturn(List.of(
                        createPreference(NotificationChannel.EMAIL),
                        createPreference(NotificationChannel.PUSH)
                ));

        notificationService.process(event);

        verify(emailStrategy).send(eq(event), any(NotificationPreference.class));
        verify(pushStrategy).send(eq(event), any(NotificationPreference.class));
        verify(telegramStrategy, never())
                .send(any(), any());
    }

    @Test
    void shouldNotSendNotificationsWhenNoChannelsEnabled() {
        TaskEvent event = createEvent();

        when(preferenceRepository.findAllByUserIdAndEnabledTrue(7L))
                .thenReturn(List.of());

        notificationService.process(event);

        verify(emailStrategy, never()).send(any(), any());
        verify(telegramStrategy, never()).send(any(), any());
        verify(pushStrategy, never()).send(any(), any());
    }

    @Test
    void shouldSendNotificationOnlyThroughTelegramWhenItIsEnabled() {
        TaskEvent event = createEvent();

        when(preferenceRepository.findAllByUserIdAndEnabledTrue(7L))
                .thenReturn(List.of(
                        createPreference(NotificationChannel.TELEGRAM)
                ));

        notificationService.process(event);

        verify(telegramStrategy).send(eq(event), any(NotificationPreference.class));
        verify(emailStrategy, never()).send(any(), any());
        verify(pushStrategy, never()).send(any(), any());
    }

    private TaskEvent createEvent() {
        return new TaskEvent(
                UUID.randomUUID(),
                TaskEventType.TASK_CREATED,
                42L,
                7L,
                "Изучить Spring",
                "user@example.com",
                LocalDateTime.now()
        );
    }

    private NotificationPreference createPreference(
            NotificationChannel channel
    ) {
        NotificationPreference preference = new NotificationPreference();
        preference.setUserId(7L);
        preference.setChannel(channel);
        preference.setEnabled(true);
        return preference;
    }
}