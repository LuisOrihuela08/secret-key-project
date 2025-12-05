package secret.key.project.mapper;

import secret.key.project.dto.PlatformCredentialDTO;
import secret.key.project.entity.PlatformCredential;

public class PlatformCredentialMapper {

    //esto para evitar instanciarla
    private PlatformCredentialMapper (){
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no debe ser instanciada");
    }

    public static PlatformCredential toEntity (PlatformCredentialDTO dto){
        PlatformCredential entity = new PlatformCredential();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setUrl(dto.getUrl());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setCreatedDate(dto.getCreatedDate());
        return entity;
    }

    public static PlatformCredentialDTO toDTO (PlatformCredential entity){
        PlatformCredentialDTO dto = new PlatformCredentialDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUrl(entity.getUrl());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
