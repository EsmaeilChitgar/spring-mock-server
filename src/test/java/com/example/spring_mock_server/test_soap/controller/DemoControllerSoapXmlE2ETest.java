package com.example.spring_mock_server.test_soap.controller;

import com.example.spring_mock_server.config.MockServerConfig;
import com.example.spring_mock_server.helper.ResourceHelper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
class DemoControllerSoapXmlE2ETest {
    @Autowired
    MockMvc mockMvc;
    private MockServerConfig mockServerConfig;

    @BeforeAll
    public void startServer() {
        mockServerConfig = new MockServerConfig();
        mockServerConfig.registerSoapEndpoint();
    }

    @AfterAll
    public void tearDown() {
        mockServerConfig.stopServer();
    }

    @Test
    void shouldReturnSuccessForSoapRequest() throws Exception {
        String soapRequest = ResourceHelper.readJsonFile("soapRequest.xml");
        MvcResult result = mockMvc.perform(post("/soap-api")
                                .contentType(MediaType.APPLICATION_JSON)
                        .content(soapRequest))
                .andExpect(status().is(200))
                .andReturn();
        String expectedResponse = ResourceHelper.readJsonFile("soapResponse.xml");
        String actualResponse = result.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }
}