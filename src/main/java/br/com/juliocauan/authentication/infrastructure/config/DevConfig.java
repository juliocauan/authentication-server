package br.com.juliocauan.authentication.infrastructure.config;

import org.openapitools.model.EmailType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.juliocauan.authentication.util.EmailService;

@Configuration
@Profile("dev")
public class DevConfig {
    
    DevConfig(EmailService emailService) {
        emailService.setEmailer("admin@authentication.dev", "admin", EmailType.GREEN_MAIL);
    }
    
}
