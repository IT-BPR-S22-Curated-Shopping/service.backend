package bpr.service.backend.configuration.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("FirebaseAdminConfig")
public class FirebaseAdminConfig {

    @Getter
    @JsonProperty("type")
    @Value("${firebase.admin.type}")
    private String type;

    @Getter
    @JsonProperty("project_id")
    @Value("${firebase.admin.project.id}")
    private String projectId;

    @Getter
    @JsonProperty("private_key_id")
    @Value("${firebase.admin.private.key.id}")
    private String privateKeyId;

    @Getter
    @JsonProperty("private_key")
    @Value("${firebase.admin.private.key}")
    private String privateKey;

    @Getter
    @JsonProperty("client_email")
    @Value("${firebase.admin.client.email}")
    private String clientEmail;

    @Getter
    @JsonProperty("client_id")
    @Value("${firebase.admin.client.id}")
    private String clientId;

    @Getter
    @JsonProperty("auth_uri")
    @Value("${firebase.admin.auth.uri}")
    private String authUri;

    @Getter
    @JsonProperty("token_uri")
    @Value("${firebase.admin.token.uri}")
    private String tokenUri;

    @Getter
    @JsonProperty("auth_provider_x509_cert_url")
    @Value("${firebase.admin.auth.provider.x509.cert.url}")
    private String authProviderCertUrl;

    @Getter
    @JsonProperty("client_x509_cert_url")
    @Value("${firebase.admin.client.x509.cert.url}")
    private String clientCertUrl;

    public FirebaseAdminConfig() {
    }
}
