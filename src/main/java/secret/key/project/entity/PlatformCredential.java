package secret.key.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "platform_credentials")
@CompoundIndex(name = "user_name_idx", def = "{'userId': 1, 'name': 1}", unique = true)
public class PlatformCredential {

    @Id
    private String id;

    @Indexed
    @Field(name = "user_id")
    private String userId;

    private String name;
    private String url;
    private String username;
    private String password;
    @Field(name = "create_date")
    private LocalDate createdDate;
}
