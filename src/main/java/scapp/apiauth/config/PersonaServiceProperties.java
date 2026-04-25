package scapp.apiauth.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.services.persona")
public class PersonaServiceProperties {

    private String baseUrl;
    private String createPersonaPath;
    private String apiKey;
}