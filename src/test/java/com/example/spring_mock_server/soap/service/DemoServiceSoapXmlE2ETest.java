package com.example.spring_mock_server.soap.service;

import com.example.spring_mock_server.config.MockServerConfig;
import com.example.spring_mock_server.helper.ResourceHelper;
import com.example.spring_mock_server.service.ServiceInvoker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
class DemoServiceSoapXmlE2ETest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ServiceInvoker serviceInvoker;
    private MockServerConfig mockServerConfig;

    @BeforeAll
    public void startServer() {
        mockServerConfig = new MockServerConfig();
        mockServerConfig.registerSoapLenEndpoint();
    }

    @AfterAll
    public void tearDown() {
        mockServerConfig.stopServer();
    }

    @Test
    void shouldReturnSoapRequestLengthWithTimeoutCheck() {
        String soapRequest = ResourceHelper.readJsonFile("soapRequest.xml");
        String[] result = new String[1];
        int expectedLength = soapRequest.length();
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            result[0] = serviceInvoker.invoke(soapRequest);
        });
        assertThat(result[0]).isEqualTo(String.valueOf(expectedLength));
    }
}