package com.example.spring_mock_server.test_rest.controller;

import com.example.spring_mock_server.config.MockServerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
class DemoControllerRestJsonE2ETest {
    @Autowired
    MockMvc mockMvc;
    private MockServerConfig mockServerConfig;

    @BeforeAll
    public void startServer() throws JsonProcessingException {
        mockServerConfig = new MockServerConfig();
        mockServerConfig.registerRestfulGetEndpoint();
    }

    @AfterEach
    public void afterEach() {
        mockServerConfig.verifyHelloWorldEndpoint();
    }

    @AfterAll
    public void tearDown() {
        mockServerConfig.stopServer();
    }

    @Test
    void shouldReturnSuccessForRestfulGet() throws Exception {
        MvcResult result = mockMvc.perform(get("/rest-api")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String actualResponse = result.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"message\": \"Hello, World!\"}");
    }
}