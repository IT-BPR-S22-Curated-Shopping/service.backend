package bpr.service.backend.configuration.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;


// https://firebase.google.com/docs/admin/setup#java_2

@Configuration
public class FirebaseAuthConfig {

    @Value("classpath:firebase-account.json")
    Resource serviceAccount;

    @Bean
    FirebaseAuth firebaseAuth() throws IOException {
        var options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build();

        var firebaseApp = FirebaseApp.initializeApp(options);

        return FirebaseAuth.getInstance(firebaseApp);
    }
}