package secret.key.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import secret.key.project.dto.PlatformCredentialDTO;
import secret.key.project.service.PlatformCredentialService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PlatformCredentialControllerTest")
@Slf4j
public class PlatformCredentialControllerTest {

    @Mock
    private PlatformCredentialService platformCredentialService;

    @InjectMocks
    private PlatformCredentialController platformCredentialController;

    private PlatformCredentialDTO platformCredentialDTO;

    @BeforeEach
    void setUp() {

        //Objeto de prueba
        platformCredentialDTO = new PlatformCredentialDTO();
        platformCredentialDTO.setId(UUID.randomUUID().toString());
        platformCredentialDTO.setName("Netflix");
        platformCredentialDTO.setUrl("https://netflix.com");
        platformCredentialDTO.setUsername("testuser");
        platformCredentialDTO.setPassword("testpassword");
        platformCredentialDTO.setCreatedDate(LocalDate.now());

    }

    @Nested
    @DisplayName("Test GET / - Debe retornar paginación exitosamente")
    class  FindPlatformCredentialsPagination{

        @Test
        void testFindPlatformCredentialsPagination(){

            Pageable pageable = PageRequest.of(0, 10);
            List<PlatformCredentialDTO> platforms = Arrays.asList(platformCredentialDTO);
            Page<PlatformCredentialDTO> mockPage = new PageImpl<>(platforms, pageable, platforms.size());

            when(platformCredentialService.getPlatformCredentialByPagination(pageable)).thenReturn(mockPage);

            ResponseEntity<Page<PlatformCredentialDTO>> result = platformCredentialController.findPlatformCredentialsPagination(0, 10);

            assertNotNull(result);
            assertNotNull(result.getBody());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(1, result.getBody().getTotalElements());
            assertEquals("Netflix", result.getBody().getContent().get(0).getName());

            verify(platformCredentialService, times(1)).getPlatformCredentialByPagination(pageable);
            log.info("Prueba de petición HTTP GET paginación de credenciales exitosa.");

        }
    }

    @Nested
    @DisplayName("Test GET /name - Debe retornar plataforma por nombre exitosamente")
    class FindPlatformCredentialsName{

        @Test
        void testFindPlatformCredentialsName(){

            when(platformCredentialService.getPlatformCredentialByName("Netflix")).thenReturn(platformCredentialDTO);

            ResponseEntity<PlatformCredentialDTO> result = platformCredentialController.findPlatformCredentialByName("Netflix");

            assertNotNull(result);
            assertNotNull(result.getBody());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Netflix", result.getBody().getName());
            assertEquals(platformCredentialDTO.getId(), result.getBody().getId().toString());
            verify(platformCredentialService, times(1)).getPlatformCredentialByName(anyString());
            log.info("Prueba de petición HTTP GET plataforma por nombre exitosa.");
        }
    }
}
