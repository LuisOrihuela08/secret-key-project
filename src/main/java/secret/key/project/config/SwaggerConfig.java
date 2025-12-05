package secret.key.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Secret Key Project")
                                            .description("Backend de Secret Key Project que permite alojar las credenciales de manera segura")
                                            .version("1.0.0."));
    }
}
