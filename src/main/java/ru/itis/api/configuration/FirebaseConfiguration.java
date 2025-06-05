package ru.itis.api.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfiguration {
    @Value("${firebase.config.json}")
    private String firebaseConfigJson;

    @PostConstruct
    public void initialize() throws IOException {
//        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");

//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                        new ByteArrayInputStream(firebaseConfigJson.getBytes())
                ))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}