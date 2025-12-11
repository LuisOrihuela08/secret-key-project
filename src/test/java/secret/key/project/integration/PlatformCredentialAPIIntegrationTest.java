package secret.key.project.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import secret.key.project.repository.PlatformCredentialRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@DisplayName("API REST Integration Test - PlatformCredential")
public class PlatformCredentialAPIIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlatformCredentialRepository platformCredentialRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0").withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        log.info("🚀 MongoDB container started for API tests!");
        log.info("📍 Connection: {}", mongoDBContainer.getReplicaSetUrl());
    }

    @BeforeEach
    void setUp() {
        platformCredentialRepository.deleteAll();
        log.info("🧹 Database cleaned before test");
    }

    @AfterEach
    void tearDown() {
        platformCredentialRepository.deleteAll();
        log.info("🧹 Database cleaned after test");
    }

    //POST
    @Nested
    @DisplayName("POST /v1/secret-key/platform")
    class createPlatformCredential {

        @Test
        @Order(1)
        @WithMockUser(username = "testuser", roles = {"USER"})
        @DisplayName("Debe crear una platforma correctamente")
        void shouldCreatePlatformCredentialSuccessfully() throws Exception {

            String userId = "user-123";
            String jsonRequest = """
                    {
                    "name": "GitHub",
                    "url": "https://github.com",
                    "username": "githubuser",
                    "password": "githubpass123",
                    "createdDate": "2024-12-10"
                    }
                    """;

            MvcResult result = mockMvc.perform(post("/v1/secret-key/platform/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("GitHub"))
                    .andExpect(jsonPath("$.url").value("https://github.com"))
                    .andExpect(jsonPath("$.username").value("githubuser"))
                    .andExpect(jsonPath("$.id").exists())
                    .andReturn();

            long count = platformCredentialRepository.count();
            assertEquals(1, count, "Debe haber 1 plataforma en la base de datos");
            log.info("✅ Debe crear una platforma correctamente");
        }

    }
}
