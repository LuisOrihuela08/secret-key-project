package secret.key.project.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import secret.key.project.entity.PlatformCredential;
import secret.key.project.repository.PlatformCredentialRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Slf4j
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@DisplayName("Prueba de Integración - PlatformCredential en MongoDB")
public class PlatformCredentialIntegrationTest {

    @Autowired
    private PlatformCredentialRepository platformCredentialRepository;

    //@Autowired
    //private ObjectMapper objectMapper;

   // @Autowired
    //private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0").withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry){
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        log.info("🚀 MongoDB container started!");
        log.info("📍 Connection: {}", mongoDBContainer.getReplicaSetUrl());
        log.info("🔌 Mapped port: {}", mongoDBContainer.getFirstMappedPort());

    }

    @BeforeEach
    void setUp(){

        String actualUri = environment.getProperty("spring.mongodb.uri");
        log.info("🔍 Spring is actually using URI: {}", actualUri);

        platformCredentialRepository.deleteAll();
        log.info("🧹 Database cleaned up before each test.");
    }

    @AfterEach
    void tearDown(){
        platformCredentialRepository.deleteAll();
        log.info("🧹 Database cleaned up after each test.");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1 - Save Plataforma Credential - MongoDB")
    void testSavePlatformCredential(){

        PlatformCredential platformCredential = new PlatformCredential();
        platformCredential.setName("Gitlab");
        platformCredential.setUrl("https://gitlab.com");
        platformCredential.setUsername("gitlab");
        platformCredential.setPassword("123");
        platformCredential.setUserId("user-123");
        platformCredential.setCreatedDate(LocalDate.now());

        log.info("💾 Saving PlatformCredential: {}", platformCredential.getName());

        PlatformCredential saved = platformCredentialRepository.save(platformCredential);

        assertNotNull(saved.getId(), "Saved PlatformCredential should have an ID");
        assertEquals("Gitlab", saved.getName());
        assertEquals("https://gitlab.com", saved.getUrl());
        assertEquals("gitlab", saved.getUsername());

        Optional<PlatformCredential> found = platformCredentialRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "PlatformCredential should be found in the database");
        assertEquals("Gitlab", found.get().getName());

        log.info("✅ PlatformCredential saved and verified successfully with ID: {}", saved.getId());
        log.info("✅ PlatformCredential details: {}", saved);
    }

}