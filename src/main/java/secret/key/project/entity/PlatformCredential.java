package secret.key.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "platform_credentials")
public class PlatformCredential {

    @Id
    private String id;
    private String name;
    private String url;
    private String username;
    private String password;
    @Field(name = "create_date")
    private LocalDate createdDate;
}
