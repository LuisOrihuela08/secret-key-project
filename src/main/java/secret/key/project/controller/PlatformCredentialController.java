package secret.key.project.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import secret.key.project.dto.PlatformCredentialDTO;
import secret.key.project.entity.PlatformCredential;
import secret.key.project.service.PlatformCredentialService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/secret-key/v1/platform")
public class PlatformCredentialController {

    private final PlatformCredentialService platformCredentialService;

    public PlatformCredentialController(PlatformCredentialService platformCredentialService){
        this.platformCredentialService =  platformCredentialService;
    }

    @GetMapping("/")
    public ResponseEntity<Page<PlatformCredentialDTO>> findPlatformCredentialsPagination(@RequestParam ("page") int page,
                                                                                         @RequestParam ("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PlatformCredentialDTO> pagination = platformCredentialService.getPlatformCredentialByPagination(pageable);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/name")
    public ResponseEntity<PlatformCredentialDTO> findPlatformCredentialByName(@RequestParam ("name") String name){
        PlatformCredentialDTO platformFind = platformCredentialService.getPlatformCredentialByName(name);
        return ResponseEntity.ok(platformFind);
    }

    @PostMapping("/")
    public ResponseEntity<PlatformCredentialDTO> createPlatformCredential (@Valid @RequestBody PlatformCredentialDTO platformCredentialDTO){
        PlatformCredentialDTO saved = platformCredentialService.createPlatformCredential(platformCredentialDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformCredentialDTO> updatePlatformCredential(@PathVariable String id, @Valid @RequestBody PlatformCredentialDTO platformCredentialDTO){
        PlatformCredentialDTO updated = platformCredentialService.updatePlarformCredential(platformCredentialDTO, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlatformCredential (@PathVariable String id){
        platformCredentialService.deletePlatformCredential(id);
        return new ResponseEntity<>(Map.of("message", "Plataforma eliminada con éxito!"), HttpStatus.OK);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportPlatformCredentialExcel() {
        byte[] excel = platformCredentialService.exportarPlataformas();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("lista-plataformas.xlsx").build());
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(excel);
    }
}
