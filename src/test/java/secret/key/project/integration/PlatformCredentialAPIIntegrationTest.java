package secret.key.project.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import secret.key.project.entity.PlatformCredential;
import secret.key.project.repository.PlatformCredentialRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import java.time.LocalDate;

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

    //GET PAGINATION
    @Nested
    @DisplayName("GET PAGINATION /v1/secret-key/platform/")
    class getPlatformCredentialsPagination {

        @Test
        @Order(1)
        @WithMockUser(username = "testuser", roles = {"USER"})
        @DisplayName("Debe obtener la paginación de plataformas correctamente")
        void getPlatformCredentialPaginationSuccessfully() throws Exception {

            for (int i = 1; i <= 10; i++) {
                PlatformCredential platforms = new PlatformCredential();
                platforms.setUserId("testuser");
                platforms.setName("Platform " + i);
                platforms.setUrl("https://platform" + i + ".com");
                platforms.setUsername("user" + i);
                platforms.setPassword("pass" + i);
                platforms.setCreatedDate(LocalDate.now());
                platformCredentialRepository.save(platforms);
            }

            MvcResult result = mockMvc.perform(get("/v1/secret-key/platform/")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(10))
                    .andExpect(jsonPath("$.totalElements").value(10))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andReturn();

            log.info("Paginación obtenida: {}", result.getResponse().getContentAsString());
            log.info("✅ Debe obtener la paginación de plataformas correctamente");
        }
    }

    //GET/name
    @Nested
    @DisplayName("GET-name /v1/secret-key/platform/name")
    class getPlatformCredentialByName {

        @Test
        @Order(2)
        @WithMockUser(username = "testuser", roles = {"USER"})
        @DisplayName("Debe obtener una platforma por nombre correctamente")
        void shouldGetPlatformCredentialByNameSuccessfully() throws Exception {

            PlatformCredential platform = new PlatformCredential();
            platform.setUserId("testuser");
            platform.setName("LinkedIn");
            platform.setUrl("https://linkedin.com");
            platform.setUsername("usertest");
            platform.setPassword("passtest");
            platform.setCreatedDate(LocalDate.now());
            platformCredentialRepository.save(platform);

            MvcResult result = mockMvc.perform(get("/v1/secret-key/platform/name")
                            .param("name", "LinkedIn")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("LinkedIn"))
                    .andExpect(jsonPath("$.url").value("https://linkedin.com"))
                    .andExpect(jsonPath("$.username").value("usertest"))
                    .andReturn();

            log.info("Plataforma obtenida: {}", result.getResponse().getContentAsString());
            log.info("Debe obtener una platforma por nombre correctamente");
        }
    }

    //POST
    @Nested
    @DisplayName("POST /v1/secret-key/platform")
    class createPlatformCredential {

        @Test
        @Order(3)
        @WithMockUser(username = "testuser", roles = {"USER"})
        @DisplayName("Debe crear una platforma correctamente")
        void shouldCreatePlatformCredentialSuccessfully() throws Exception {

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
            log.info("Plataforma creada: {}", result.getResponse().getContentAsString());
            log.info("✅ Debe crear una platforma correctamente");
        }
    }

    //PUT
    @Nested
    @DisplayName("PUT /v1/secret-key/platform/{id}")
    class updatePlatformCredential{

        @Test
        @Order(4)
        @WithMockUser(username = "testuser", roles = {"USER"})
        @DisplayName("Debe actualizar una plataforma correctamente")
        void shouldUpdatePlatformCredentialSuccessfully() throws Exception{

            PlatformCredential platform = new PlatformCredential();
            platform.setUserId("testuser");
            platform.setName("LinkedIn");
            platform.setUrl("https://linkedin.com");
            platform.setUsername("usertest");
            platform.setPassword("passtest");
            platform.setCreatedDate(LocalDate.now());
            platformCredentialRepository.save(platform);

            MvcResult result = mockMvc.perform(put("/v1/secret-key/platform/{id}", platform.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    """
                                    {
                                    "name": "LinkedIn",
                                    "url": "https://linkedin.com",
                                    "username": "usertest",
                                    "password": "newpasstest",
                                    "createdDate": "2024-12-10"
                                    }
                                    """))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("LinkedIn"))
                    .andExpect(jsonPath("$.url").value("https://linkedin.com"))
                    .andExpect(jsonPath("$.username").value("usertest"))
                    .andExpect(jsonPath("$.id").exists())
                    .andReturn();

            log.info("Plataforma actualizada: {}", result.getResponse().getContentAsString());
            log.info("✅ Debe actualizar una plataforma correctamente");
        }
    }
}
