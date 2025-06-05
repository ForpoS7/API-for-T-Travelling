package ru.itis.api.util;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class MessageFactory {
    public static Message createStandardMessage(String deviceToken, String title, String body) {
        if (deviceToken == null || deviceToken.isEmpty()) {
            throw new IllegalArgumentException("Device token must not be null or empty");
        }
        return Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
    }

    public static Message createMessageWithData(String deviceToken, String title, String body, Map<String, String> data) {
        if (deviceToken == null || deviceToken.isEmpty()) {
            throw new IllegalArgumentException("Device token must not be null or empty");
        }
        return Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .build();
    }
}
