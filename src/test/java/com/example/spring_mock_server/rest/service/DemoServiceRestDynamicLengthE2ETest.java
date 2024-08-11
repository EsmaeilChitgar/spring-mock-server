package com.example.spring_mock_server.rest.service;

import com.example.spring_mock_server.config.MockServerConfig;
import com.example.spring_mock_server.exception.EmptyValueException;
import com.example.spring_mock_server.exception.InvalidNumericException;
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
class DemoServiceRestDynamicLengthE2ETest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ServiceInvoker serviceInvoker;
    private MockServerConfig mockServerConfig;

    @BeforeAll
    public void startServer() {
        mockServerConfig = new MockServerConfig();
        mockServerConfig.registerDynamicLengthEndpoint();
    }

    @AfterAll
    public void tearDown() {
        mockServerConfig.stopServer();
    }

    @Test
    void shouldReturnSuccessForPostRequest() {
        String inputValue = "Hello, World!";
        int expectedLength = inputValue.length();
        String result = serviceInvoker.invoke(inputValue);
        assertThat(result).isEqualTo(String.valueOf(expectedLength));
    }

    @Test
    void shouldReturnSuccessForPostRequestWithTimeoutCheck() {
        String inputValue = "Hello, World!";
        String[] result = new String[1];
        int expectedLength = inputValue.length();
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            result[0] = serviceInvoker.invoke(inputValue);
        });
        assertThat(result[0]).isEqualTo(String.valueOf(expectedLength));
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        Assertions.assertThrows(EmptyValueException.class, () -> serviceInvoker.invoke(""));
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        Assertions.assertThrows(EmptyValueException.class, () -> serviceInvoker.invoke(null));
    }

    @Test
    void shouldThrowExceptionForInvalidNumericValue() {
        Assertions.assertThrows(InvalidNumericException.class, () -> serviceInvoker.invoke("10"));
    }
}