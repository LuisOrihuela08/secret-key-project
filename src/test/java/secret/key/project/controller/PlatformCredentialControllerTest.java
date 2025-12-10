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
import java.util.Map;
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

    @Nested
    @DisplayName("Test POST / - Debe crear plataforma exitosamente")
    class createPlatformCredential{

        @Test
        void testCreatePlatformCredential(){

            when(platformCredentialService.createPlatformCredential(any(PlatformCredentialDTO.class))).thenReturn(platformCredentialDTO);

            ResponseEntity<PlatformCredentialDTO> result = platformCredentialController.createPlatformCredential(platformCredentialDTO);

            assertNotNull(result);
            assertNotNull(result.getBody());
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertEquals("Netflix", result.getBody().getName());
            verify(platformCredentialService, times(1)).createPlatformCredential(platformCredentialDTO);
            log.info("Prueba de petición HTTP POST crear plataforma exitosa.");
        }
    }

    @Nested
    @DisplayName("Test PUT /{id} - Debe actualizar plataforma exitosamente")
    class updatePlatformCredential{

        @Test
        void testUpdatePlatformCredential(){

            //Objeto de prueba
            PlatformCredentialDTO updatePlatformCredential = new PlatformCredentialDTO();
            //updatePlatformCredential.setId(UUID.randomUUID().toString());
            updatePlatformCredential.setName("Netflix");
            updatePlatformCredential.setUrl("https://netflix.com");
            updatePlatformCredential.setUsername("testuser");
            updatePlatformCredential.setPassword("newtestpassword");
            updatePlatformCredential.setCreatedDate(LocalDate.now());

            when(platformCredentialService.updatePlarformCredential(updatePlatformCredential, platformCredentialDTO.getId())).thenReturn(updatePlatformCredential);

            ResponseEntity<PlatformCredentialDTO> result = platformCredentialController.updatePlatformCredential(platformCredentialDTO.getId(), updatePlatformCredential);

            assertNotNull(result);
            assertNotNull(result.getBody());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Netflix", result.getBody().getName());
            assertEquals("newtestpassword", result.getBody().getPassword());
            verify(platformCredentialService, times(1)).updatePlarformCredential(updatePlatformCredential, platformCredentialDTO.getId());
            log.info("Prueba de petición HTTP PUT actualizar plataforma exitosa.");
        }
    }

    @Nested
    @DisplayName("Test DELETE /{id} - Debe eliminar plataforma exitosamente")
    class deletePlatformCredential{

        @Test
        void testDeletePlatformCredential(){

            doNothing().when(platformCredentialService).deletePlatformCredential(platformCredentialDTO.getId());

            ResponseEntity<Map<String, String>> result = platformCredentialController.deletePlatformCredential(platformCredentialDTO.getId());

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Plataforma eliminada con éxito!", result.getBody().get("message"));
            verify(platformCredentialService, times(1)).deletePlatformCredential(platformCredentialDTO.getId());
            log.info("Prueba de petición HTTP DELETE eliminar plataforma exitosa.");
        }
    }

    @Nested
    @DisplayName("Test GET /export/excel - Debe exportar plataformas a Excel exitosamente")
    class exportPlatformCredentialExcel{

        @Test
        void testExportPlatformCredentialExcel(){

            byte[] mockExcel = new byte[]{0x50, 0x4B, 0x03, 0x04}; // Simulación de archivo Excel en bytes

            when(platformCredentialService.exportarPlataformasExcel()).thenReturn(mockExcel);

            ResponseEntity<byte[]> result = platformCredentialController.exportPlatformCredentialExcel();

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(mockExcel, result.getBody());
            verify(platformCredentialService, times(1)).exportarPlataformasExcel();
            log.info("Prueba de petición HTTP GET exportar plataformas a Excel exitosa.");
        }
    }

    @Nested
    @DisplayName("Test GET /export/pdf - Debe exportar plataformas a PDF exitos")
    class exportPlatformCredentialPdf{

        @Test
        void testExportPlatformCredentialPdf(){

            byte[] mockPdf = new byte[]{0x25, 0x50, 0x44, 0x46}; // Simulación de archivo PDF en bytes

            when(platformCredentialService.exportarPlataformasPDF()).thenReturn(mockPdf);

            ResponseEntity<byte[]> result = platformCredentialController.exportPlatformCredentialPdf();

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(mockPdf, result.getBody());
            verify(platformCredentialService, times(1)).exportarPlataformasPDF();
            log.info("Prueba de petición HTTP GET exportar plataformas a PDF exitosa.");
        }
    }
}
