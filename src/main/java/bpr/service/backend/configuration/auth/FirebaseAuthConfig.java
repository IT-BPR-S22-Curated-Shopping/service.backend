package bpr.service.backend.configuration.auth;

import bpr.service.backend.util.ISerializer;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


// https://firebase.google.com/docs/admin/setup#java_2

@Configuration
public class FirebaseAuthConfig {

    private final FirebaseAdminConfig adminConfig;
    private final ISerializer serializer;

    public FirebaseAuthConfig(@Autowired @Qualifier("FirebaseAdminConfig") FirebaseAdminConfig adminConfig,
                              @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.adminConfig = adminConfig;
        this.serializer = serializer;
    }

    @Bean
    protected FirebaseAuth firebaseAuth() throws IOException {
        var options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(serializer.toJson(adminConfig).getBytes(StandardCharsets.UTF_8))))
                .build();

        var firebaseApp = FirebaseApp.initializeApp(options);

        return FirebaseAuth.getInstance(firebaseApp);
    }
}