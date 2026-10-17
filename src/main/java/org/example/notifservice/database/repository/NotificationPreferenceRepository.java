package org.example.notifservice.database.repository;

import org.example.notifservice.database.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findAllByUserIdAndEnabledTrue(Long userId);
}