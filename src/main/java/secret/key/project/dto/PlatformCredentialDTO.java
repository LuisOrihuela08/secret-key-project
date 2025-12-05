package secret.key.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
public class PlatformCredentialDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)//Esto me oculta el id para el POST y PUT
    private String id;
    private String name;
    private String url;
    private String username;
    private String password;
    private LocalDate createdDate;
}
