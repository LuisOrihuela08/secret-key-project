package secret.key.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import secret.key.project.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername (String username);
}
