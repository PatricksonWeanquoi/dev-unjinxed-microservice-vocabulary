package dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.oxforddictionaries;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.oxforddictionaries.impl.OxfordDictionariesAdapterImpl;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionariesResponse;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import dev.unjinxed.unjinxedmicroservices.utils.MockitoInit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@DisplayName("Test-Case: Oxford Dictionaries Adapter")
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
class OxfordDictionariesAdapterTest extends MockitoInit{
    OxfordDictionariesAdapterImpl oxfordDictionariesAdapterImpl;
    @Value("${oxfordDictionaries.url}")
    String url;
    @Value("${oxfordDictionaries.app_id}")
    String appId;
    @Value("${oxfordDictionaries.app_key}")
    String appKey;

    @BeforeEach
    void init() {
        oxfordDictionariesAdapterImpl = Mockito.spy(new OxfordDictionariesAdapterImpl(url, appId, appKey));
    }
    @ParameterizedTest
    @MethodSource("responses")
    @DisplayName("Get Word Definition Get Success Definition")
    void getWordDefinitionGetSuccessDefinitionTest(Mono<Object> responseInput) {
        doReturn(responseInput).when((oxfordDictionariesAdapterImpl)).triggerServiceCallOut(any(), any());
        Mono<OxfordDictionariesResponse> oxfordDictionariesResponseMono = oxfordDictionariesAdapterImpl.getWordDefinition("test");
        StepVerifier.create(oxfordDictionariesResponseMono)
                .assertNext(response -> assertNotNull(response, "serviceCallOutSuccessGetCall(): Response not null")
                ).verifyComplete();
    }

    @Test
    @DisplayName("Get Word Definition Throw Exception")
    void getWordDefinitionThrowExceptionTest() throws RequestEntityBuilderException {
        Mockito.doThrow(new RequestEntityBuilderException("URL format error")).when(oxfordDictionariesAdapterImpl).createRequestEntity(any(), any(), any(), any());
        Mono<OxfordDictionariesResponse> randomWordsResponseMono = this.oxfordDictionariesAdapterImpl.getWordDefinition("test");
        StepVerifier.create(randomWordsResponseMono)
                .verifyError();
    }

    static Stream<Arguments> responses() {
        return Stream.of(
                Arguments.of(Mono.just(new ResponseEntity<>(OxfordDictionariesResponse.builder().build(), new LinkedMultiValueMap<>(), 200)))
        );
    }
}
