package org.example;

import java.util.UUID;

public class NotificationService {
    public void sendNotification(UUID userId, String message) {
        System.out.println("Notification to user " + userId + ": " + message);
    }
}