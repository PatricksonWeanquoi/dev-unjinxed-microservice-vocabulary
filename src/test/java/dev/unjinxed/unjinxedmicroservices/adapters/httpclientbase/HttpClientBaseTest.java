package dev.unjinxed.unjinxedmicroservices.adapters.httpclientbase;

import dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.HttpClientBase;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import dev.unjinxed.unjinxedmicroservices.utils.MockitoInit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Test-Case: Http Client Base")
class HttpClientBaseTest extends MockitoInit {
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    HttpClientBase httpClientBase;

    @Tag("http-client-base")
    @DisplayName("Service Call Out Success Get Call")
    @ParameterizedTest
    @MethodSource("responses")
    void serviceCallOutSuccessGetCallTest(ResponseEntity<Object> responseEntity) throws RequestEntityBuilderException {
        log.debug("serviceCallOutSuccessGetCallTest(): entering test ... ");
        ParameterizedTypeReference parameterizedTypeReference = new ParameterizedTypeReference<String>() {};
        httpClientBase.setDomain("http://unjinxed.dev");
        String resource = "/get";
        MultiValueMap<String, String> headers= new LinkedMultiValueMap<>();
        HttpMethod method = HttpMethod.GET;
        String body = null;
        RequestEntity requestEntity = httpClientBase.generateRequestEntity(resource, body, headers, method);
        Mockito.when(restTemplate.exchange(requestEntity, parameterizedTypeReference))
                .thenReturn(responseEntity);
        StepVerifier.create(this.httpClientBase.serviceCallOut(requestEntity, parameterizedTypeReference))
                .assertNext(response -> {
                    assertNotNull(response, "serviceCallOutSuccessGetCall(): Response not null");
                    assertEquals(((ResponseEntity) response).getBody().toString(), responseEntity.getBody().toString());
                }).verifyComplete();
    }

    Stream<ResponseEntity<Object>> responses() {
        ResponseEntity getSuccess = new ResponseEntity("Hello Word", new LinkedMultiValueMap<>(), 200);
        ResponseEntity getFailed = new ResponseEntity("Bad Request", new LinkedMultiValueMap<>(), 400);
        return Stream.of(getSuccess, getFailed);
    }

}
