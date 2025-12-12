package secret.key.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfiguration {

    private final String frontendurl = "http://localhost:3000";

    @Bean
    public WebMvcConfigurer corsConfiguration(){
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry corsRegistry){
                corsRegistry.addMapping("/**")
                        .allowedOrigins(frontendurl)
                        .allowedMethods("GET", "POST","PUT","DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true) // Muy importante para JWT
                        .maxAge(3600);
            }
        };
    }
}
