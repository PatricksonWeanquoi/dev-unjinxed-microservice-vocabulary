package dev.unjinxed.unjinxedmicroservices.adapters.httpclientbase;

import dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.HttpClientBase;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import dev.unjinxed.unjinxedmicroservices.utils.MockitoInit;
import io.micrometer.core.ipc.http.HttpSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Test-Case: Http Client Base")
class HttpClientBaseTest extends MockitoInit {
    @Mock
    WebClient webClient;
    @InjectMocks
    HttpClientBase httpClientBase;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Tag("http-client-base")
    @DisplayName("Service Call Out Success Get Call")
    @ParameterizedTest
    @ValueSource(strings = {"Hello World"})
    void serviceCallOutSuccessGetCallTest(String responseBody) throws RequestEntityBuilderException, URISyntaxException {
        log.debug("serviceCallOutSuccessGetCallTest(): entering test ... ");
        final ParameterizedTypeReference<String> parameterizedTypeReference = new ParameterizedTypeReference<>() {};
        httpClientBase.setDomain("http://unjinxed.dev");
        final String resource = "/get";
        final MultiValueMap<String, String> headers= new LinkedMultiValueMap<>();
        final HttpMethod method = HttpMethod.GET;
        final RequestEntity<Object> requestEntity = httpClientBase.generateRequestEntity(resource, null, headers, method);
        final URI requestUri = new URI(httpClientBase.getDomain() + resource);
        when(webClient.method(method)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(requestUri)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(responseBody));
        StepVerifier.create(this.httpClientBase.serviceCallOut(requestEntity, parameterizedTypeReference))
                .assertNext((String response) -> {
                    assertNotNull(response, "serviceCallOutSuccessGetCall(): Response not null");
                    assertEquals(response, responseBody);
                }).verifyComplete();
    }
}
