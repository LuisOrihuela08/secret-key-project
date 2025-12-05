package secret.key.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import secret.key.project.entity.PlatformCredential;

import java.util.List;
import java.util.Optional;

public interface PlatformCredentialRepository extends MongoRepository<PlatformCredential, String> {

    Page<PlatformCredential> findAll (Pageable pageable);
    Optional<PlatformCredential>  findByName (String name);

}
