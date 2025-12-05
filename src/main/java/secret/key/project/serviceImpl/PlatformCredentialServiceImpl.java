package secret.key.project.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import secret.key.project.dto.PlatformCredentialDTO;
import secret.key.project.entity.PlatformCredential;
import secret.key.project.error.PlatformCredentialNoEncontradoException;
import secret.key.project.mapper.PlatformCredentialMapper;
import secret.key.project.repository.PlatformCredentialRepository;
import secret.key.project.service.PlatformCredentialService;

@Service
@Slf4j
public class PlatformCredentialServiceImpl implements PlatformCredentialService {

    private final PlatformCredentialRepository platformCredentialRepository;

    public PlatformCredentialServiceImpl(PlatformCredentialRepository platformCredentialRepository){
        this.platformCredentialRepository = platformCredentialRepository;
    }

    @Override
    public Page<PlatformCredentialDTO> getPlatformCredentialByPagination(Pageable pageable) {
        log.info("Listado con paginación de las platformas OK!");
        return platformCredentialRepository.findAll(pageable).map(PlatformCredentialMapper::toDTO);
    }

    @Override
    public PlatformCredentialDTO createPlatformCredential(PlatformCredentialDTO platformCredentialDTO) {

        if(platformCredentialDTO == null){
            throw new IllegalArgumentException("La plataforma no puede ser nula!");
        }

        PlatformCredential entity = PlatformCredentialMapper.toEntity(platformCredentialDTO);
        PlatformCredential saved =  platformCredentialRepository.save(entity);

        log.info("Plataforma registrada: {}", saved);
        return PlatformCredentialMapper.toDTO(saved);
    }

    @Override
    public PlatformCredentialDTO updatePlarformCredential(PlatformCredentialDTO platformCredentialDTO, String id) {

    if (id == null){
        throw new IllegalArgumentException("El id no puede ser nulo!!");
    }

    PlatformCredential entity = platformCredentialRepository.findById(id).orElseThrow(() -> {
        log.error("Error al actualizar, plataforma no encontrada con el id: {}", id);
        return new PlatformCredentialNoEncontradoException("Plataforma no encontrada! con el id: " + id);
    });

    entity.setName(platformCredentialDTO.getName());
    entity.setUrl(platformCredentialDTO.getUrl());
    entity.setUsername(platformCredentialDTO.getUsername());
    entity.setPassword(platformCredentialDTO.getPassword());
    entity.setCreatedDate(platformCredentialDTO.getCreatedDate());

    PlatformCredential saved = platformCredentialRepository.save(entity);

    log.info("Plataforma actualizada: {}", saved);
    return PlatformCredentialMapper.toDTO(saved);
    }

    @Override
    public void deletePlatformCredential(String id) {

        if (id == null){
            throw new IllegalArgumentException("El id no puede ser nulo!!");
        } else if (!platformCredentialRepository.existsById(id)){
            log.error("Error al eliminar, plataforma no encontrada con el id: {}", id);
            throw new PlatformCredentialNoEncontradoException("Plataforma no encontrada con el id:" + id);
        }

        platformCredentialRepository.deleteById(id);
        log.info("Plataforma eliminada!");
    }

    @Override
    public PlatformCredentialDTO getPlatformCredentialByName(String name) {

        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacio!!");
        }

        PlatformCredential entity = platformCredentialRepository.findByName(name).orElseThrow(() -> {
            log.error("Error al buscar, plataforma no encontrada con el nombre: {}", name);
            return new PlatformCredentialNoEncontradoException("Plataforma no encontrada con el nombre: " + name);
        });

        log.info("Plataforma encontrada: {}", entity);
        return PlatformCredentialMapper.toDTO(entity);
    }
}
