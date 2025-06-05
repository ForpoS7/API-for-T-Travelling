package ru.itis.api.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.api.entity.Transaction;
import ru.itis.api.entity.Travel;
import ru.itis.api.entity.UserTransaction;
import ru.itis.api.entity.UserTravel;
import ru.itis.api.util.MessageFactory;

import java.util.Map;

@Service
@Slf4j
public class NotificationService {
    public void notifyPayment(Transaction transaction) {
        transaction.getUsers()
                .stream()
                .filter(userTransaction -> !userTransaction.getIsRepaid())
                .map(UserTransaction::getUser)
                .forEach(user -> {
                    try {
                        sendStandardNotification(
                                user.getDeviceToken(),
                                "Payment reminder",
                                "Pay off the debt for " + transaction.getCategory()
                        );
                    } catch (Exception e) {
                        log.error("Failed to send notification to user with device token: {}", user.getDeviceToken(), e);
                    }
                });
    }

    public void notifyConfirmTravel(Travel travel){
        travel.getUsers()
                .stream()
                .filter(userTravel -> !userTravel.getIsConfirmed())
                .map(UserTravel::getUser)
                .forEach(user -> {
                    try {
                        sendNotificationWithData(
                                user.getDeviceToken(),
                                "Confirm participation in " + travel.getName() + ".",
                                "You have been invited by a " + travel.getCreator().getFirstName()
                                        + " "
                                        + travel.getCreator().getLastName()
                                        + " to take part in a new travel.",
                                Map.of(
                                        "travelId", String.valueOf(travel.getId())
                                ));
                    } catch (Exception e) {
                        log.error("Failed to send notification to user with device token: {}", user.getDeviceToken(), e);
                    }
                });
    }

    private void sendStandardNotification(String deviceToken, String title, String body) {
        Message message = MessageFactory.createStandardMessage(deviceToken, title, body);
        tryToSendMessage(message);
    }

    private void sendNotificationWithData(String deviceToken, String title, String body, Map<String, String> data) {
        Message message = MessageFactory.createMessageWithData(deviceToken, title, body, data);
        tryToSendMessage(message);
    }

    private void tryToSendMessage(Message message) {
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Message sent successfully: {}", response);
        } catch (Exception e) {
            log.error("Message failed to send", e);
        }
    }
}
