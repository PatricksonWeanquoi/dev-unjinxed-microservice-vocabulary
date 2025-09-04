package dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.wordsapiadapter;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.wordsapiadapter.impl.WordsAPIAdapterImpl;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.enums.WordEnum;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi.WordResponse;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
@DisplayName("Test-Case: Random Words Adapter ")
@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.yml")
class RandomWordsAdapterTest {
    WordsAPIAdapterImpl wordsAPIAdapterImpl;
    @Value("${rapidApi.wordsApi.xRapidAPIHost}")
    String appHost;
    @Value("${rapidApi.xRapidAPIKey}")
    String appKey;
    @Value("${rapidApi.wordsApi.xRapidAPIDomain}")
    String appDomain;

    @BeforeEach
    void init() {
        this.wordsAPIAdapterImpl = Mockito.spy(new WordsAPIAdapterImpl(appHost,appKey, appDomain));
    }
    @ParameterizedTest
    @MethodSource("responses")
    @DisplayName("Get Random Word Success Response")
    void getRandomWordSuccessResponseTest(Mono<WordResponse> responseInput, String word) {
        Mockito.doReturn(responseInput).when(wordsAPIAdapterImpl).triggerServiceCallOut(any(), any());
        Mono<WordResponse> randomWordsResponseMono = this.wordsAPIAdapterImpl.getWord(WordEnum.RANDOM, null);
        StepVerifier.create(randomWordsResponseMono)
                .assertNext(res -> {
                    assertNotNull(res, "Response is not null ");
                    assertThat("Response word should match", word, equalTo(res.getWord()));
                }).verifyComplete();
    }
    @Test
    @DisplayName("Get Random Word Throw Exception")
    void getRandomWordThrowExceptionTest() throws RequestEntityBuilderException {
        Mockito.doThrow(new RequestEntityBuilderException("URL format error")).when(wordsAPIAdapterImpl).createRequestEntity(any(), any(), any(), any());
        Mono<WordResponse> randomWordsResponseMono = this.wordsAPIAdapterImpl.getWord(WordEnum.RANDOM, null);
        StepVerifier.create(randomWordsResponseMono)
                .verifyError();
    }

    static Stream<Arguments> responses() {
        return Stream.of(
                Arguments.of(Mono.just(WordResponse.builder().word("dev").build()), "dev")
        );
    }
}
