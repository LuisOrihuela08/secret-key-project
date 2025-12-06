package secret.key.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import secret.key.project.dto.PlatformCredentialDTO;
import secret.key.project.entity.PlatformCredential;

import java.util.List;

public interface PlatformCredentialService {

    Page<PlatformCredentialDTO> getPlatformCredentialByPagination(Pageable pageable);
    PlatformCredentialDTO createPlatformCredential(PlatformCredentialDTO platformCredentialDTO);
    PlatformCredentialDTO updatePlarformCredential(PlatformCredentialDTO platformCredentialDTO, String id);
    void deletePlatformCredential(String id);
    PlatformCredentialDTO getPlatformCredentialByName (String name);
    List<PlatformCredentialDTO> getAllPlatformCredentials();
    byte [] exportarPlataformasExcel ();
    byte [] exportarPlataformasPDF();
}
