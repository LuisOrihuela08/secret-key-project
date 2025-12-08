package secret.key.project.serviceImpl;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import secret.key.project.dto.PlatformCredentialDTO;
import secret.key.project.entity.PlatformCredential;
import secret.key.project.entity.User;
import secret.key.project.error.UsuarioException;
import secret.key.project.error.UsuarioExceptionNoContentException;
import secret.key.project.repository.PlatformCredentialRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PlatformCredentialServiceImpl Test")
@Slf4j
public class PlatformCredentialServiceImplTest {

    @Mock
    private PlatformCredentialRepository platformCredentialRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PlatformCredentialServiceImpl platformCredentialServiceImpl;

    private User mockUser;
    private String userId;
    private PlatformCredential platformCredential;
    private PlatformCredentialDTO platformCredentialDTO;

    //Configuración incial y común para las pruebas
    @BeforeEach
    void setUp(){

        //userId = "user-123";
        userId = UUID.randomUUID().toString();
        mockUser = new User();
        mockUser.setId(userId);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        //Datos de prueba
        platformCredential = new PlatformCredential();
        platformCredential.setId(UUID.randomUUID().toString());
        platformCredential.setName("Github");
        platformCredential.setUrl("https://github.com");
        platformCredential.setUsername("user_github");
        platformCredential.setPassword("pasword123");
        platformCredential.setUserId(userId);
        platformCredential.setCreatedDate(LocalDate.now());

        platformCredentialDTO = new PlatformCredentialDTO();
        platformCredentialDTO.setId(UUID.randomUUID().toString());
        platformCredentialDTO.setName("Github");
        platformCredentialDTO.setUrl("https://github.com");
        platformCredentialDTO.setUsername("user_github");
        platformCredentialDTO.setPassword("pasword123");
        platformCredentialDTO.setCreatedDate(LocalDate.now());
    }

    @Nested //esto agrupa pruebas relacionadas
    @DisplayName("Test de Autenticación")
    class AuthenticationTests {

        @Test
        @DisplayName("Debe lanzar excepcion cuando el usuario no esta autenticado")
        void shouldThrowExceptionWhenUserNotAuthenticated(){
            when(authentication.isAuthenticated()).thenReturn(false);

            assertThrows(UsuarioException.class, () -> {
                platformCredentialServiceImpl.getAllPlatformCredentials();
            });
            log.info("Prueba de usuario no autenticado pasada correctamente.");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando autenticación es null")
        void shouldThrowExceptionWhenAuthenticationIsNull() {
            when(securityContext.getAuthentication()).thenReturn(null);

            assertThrows(UsuarioException.class, () -> {
                platformCredentialServiceImpl.getAllPlatformCredentials();
            });
            log.info("Prueba de usuario no autenticado pasada null.");
        }
    }

    @Nested
    @DisplayName("Test de getPlatformCredentialByPagination")
    class GetPlatformCredentialByPaginationTests {

        @Test
        @DisplayName("Debe retornar la paginación de las credenciales de las plataformas correctamente")
        void shouldReturnPlatformCredentialsPaginationSuccessfully() {
            Pageable pageable = PageRequest.of(0, 10);
            List<PlatformCredential> platformsCredentials = Arrays.asList(platformCredential);
            Page<PlatformCredential> page = new PageImpl<>(platformsCredentials, pageable, 1);

            when(platformCredentialRepository.findByUserId(pageable, userId)).thenReturn(page);

            Page<PlatformCredentialDTO> result = platformCredentialServiceImpl.getPlatformCredentialByPagination(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Github", result.getContent().get(0).getName());
            verify(platformCredentialRepository, times(1)).findByUserId(pageable, userId);
            log.info("Prueba de paginación de credenciales de plataformas pasada correctamente.");
        }

        @Test
        @DisplayName("Debe lanzar una excepción cuando la pagina es negativa y el tamaño es igual o menor a 0")
        void shouldThrowExceptionWhenPageIsNegativeAndSizeIsZeroOrLess() {

            assertThrows(IllegalArgumentException.class, () -> {
                PageRequest.of(-1, 10);
            });
            log.info("Prueba de validación de Spring PageRequest pasada correctamente.");
        }

        @Test
        @DisplayName("Debe lanzar una excepción cuando la página este vacia")
        void shouldThrowExceptionWhenPageIsEmpty(){
            Pageable pageable = PageRequest.of(0, 10);
            Page<PlatformCredential> emptyPage = new PageImpl<>(Collections.emptyList());

            when(platformCredentialRepository.findByUserId(pageable, userId)).thenReturn(emptyPage);

            assertThrows(UsuarioExceptionNoContentException.class, () -> {
                platformCredentialServiceImpl.getPlatformCredentialByPagination(pageable);
            });
            log.info("Prueba de página vacia pasada correctamente");
        }
    }
}
