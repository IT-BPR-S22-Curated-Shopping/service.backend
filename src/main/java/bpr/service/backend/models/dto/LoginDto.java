package bpr.service.backend.models.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class LoginDto {

    private Boolean success;
    private HttpStatus code;
    private String email;
    private String message;
    private String jwtToken;

    public LoginDto() {
    }

    public LoginDto(Boolean success, HttpStatus code, String email, String message) {
        this.success = success;
        this.code = code;
        this.email = email;
        this.message = message;
        jwtToken = "N/A";
    }

    public LoginDto(Boolean success, HttpStatus code, String email, String message, String jwtToken) {
        this.success = success;
        this.code = code;
        this.email = email;
        this.message = message;
        this.jwtToken = jwtToken;
    }
}

