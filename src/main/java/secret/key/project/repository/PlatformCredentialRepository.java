package secret.key.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import secret.key.project.entity.PlatformCredential;

import java.util.List;
import java.util.Optional;

public interface PlatformCredentialRepository extends MongoRepository<PlatformCredential, String> {

    Page<PlatformCredential> findByUserId (Pageable pageable, String userId);
    Optional<PlatformCredential> findByNameAndUserId (String name, String userId);
    List<PlatformCredential> findByUserId (String userId);
    Optional<PlatformCredential> findByIdAndUserId (String id, String userId);
    boolean existsByUserIdAndName (String userId, String name);

}
