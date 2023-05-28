package dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.randomwordsadapter;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.randomwordsadapter.impl.RandomWordsAdapterImpl;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.randomwords.RandomWordsResponse;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
@DisplayName("Test-Case: Random Words Adapter ")
@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
class RandomWordsAdapterTest {
    RandomWordsAdapterImpl randomWordsAdapterImpl;
    @Value("${X-RapidAPI-Host}")
    String appHost;
    @Value("${X-RapidAPI-Key}")
    String appKey;
    @Value("${X-RapidAPI-Domain}")
    String appDomain;

    @BeforeEach
    void init() {
        this.randomWordsAdapterImpl = Mockito.spy(new RandomWordsAdapterImpl(appHost,appKey, appDomain));
    }
    @ParameterizedTest
    @MethodSource("responses")
    @DisplayName("Get Random Word Success Response")
    void getRandomWordSuccessResponseTest(Mono<Object> responseInput, String word) {
        Mockito.doReturn(responseInput).when(randomWordsAdapterImpl).triggerServiceCallOut(any(), any());
        Mono<RandomWordsResponse> randomWordsResponseMono = this.randomWordsAdapterImpl.getRandomWord();
        StepVerifier.create(randomWordsResponseMono)
                .assertNext((res) -> {
                    assertNotNull(res, "Response is not null ");
                    assertThat("Response word should match", word, equalTo(res.getWord()));
                }).verifyComplete();
    }
    @Test
    @DisplayName("Get Random Word Throw Exception")
    void getRandomWordThrowExceptionTest() throws RequestEntityBuilderException {
        Mockito.doThrow(new RequestEntityBuilderException("URL format error")).when(randomWordsAdapterImpl).createRequestEntity(any(), any(), any(), any());
        Mono<RandomWordsResponse> randomWordsResponseMono = this.randomWordsAdapterImpl.getRandomWord();
        StepVerifier.create(randomWordsResponseMono)
                .verifyError();
    }

    static Stream<Arguments> responses() {
        return Stream.of(
                Arguments.of(Mono.just(new ResponseEntity<String>("dev", new LinkedMultiValueMap<>(), 200)), "dev"),
                Arguments.of(Mono.just(new ResponseEntity<String>("unjinxed", new LinkedMultiValueMap<>(), 200)), "unjinxed")
        );
    }
}
