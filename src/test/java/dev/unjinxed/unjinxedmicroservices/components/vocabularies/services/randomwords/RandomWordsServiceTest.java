package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.randomwordsadapter.RandomWordsAdapter;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.randomwords.RandomWordsResponse;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.impl.RandomWordsServiceImpl;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import dev.unjinxed.unjinxedmicroservices.utils.MockitoInit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
@DisplayName("Test-Case: Random Words Service")
class RandomWordsServiceTest extends MockitoInit {
    @Mock
    RandomWordsAdapter randomWordsAdapter;
    @InjectMocks
    RandomWordsServiceImpl randomWordsService;


    @DisplayName("Get Random Word Success Test")
    @ParameterizedTest
    @MethodSource("successResponses")
    void getRandomWordSuccessTest(RandomWordsResponse randomWordsResponse, String word) {
        Mockito.when(randomWordsAdapter.getRandomWord()).thenReturn(Mono.just(randomWordsResponse));
        Mono<RandomWordsResponse> randomWordsResponseMono = randomWordsService.getRandomWord();
        StepVerifier.create(randomWordsResponseMono)
                .assertNext((randomWordsResp -> {
                    assertThat("Random Words Response is not null", randomWordsResp, is(notNullValue()));
                    assertThat("Has word defined ", randomWordsResp.getWord(), is(word));
                }))
                .verifyComplete();
    }

    @DisplayName("Get Random Word Success Server Response Test")
    @ParameterizedTest
    @MethodSource("successResponses")
    void getRandomWordSuccessServerResponseTest(RandomWordsResponse randomWordsResponse) {
        Mockito.when(randomWordsAdapter.getRandomWord()).thenReturn(Mono.just(randomWordsResponse));
        Mono<ServerResponse> serverResponseMono = randomWordsService.getRandomWordServerResponse();
        StepVerifier.create(serverResponseMono)
                .assertNext((serverResponse -> {
                    assertThat("Server Response is not null", serverResponse, is(notNullValue()));
                    assertThat("Server Response status code should 200", serverResponse.statusCode().value(), equalTo(200));
                }))
                .verifyComplete();
    }

    @DisplayName("Get Random Word Exception Server Response Test")
    @ParameterizedTest
    @MethodSource("exceptionResponses")
    void getRandomWordExceptionServerResponseTest(Throwable exception, Integer statusCode) {
        Mockito.when(randomWordsAdapter.getRandomWord()).thenReturn(Mono.error(exception));
        Mono<ServerResponse> serverResponseMono = randomWordsService.getRandomWordServerResponse();
        StepVerifier.create(serverResponseMono)
                .assertNext((serverResponse -> {
                    assertThat("Server Response is not null", serverResponse, is(notNullValue()));
                    assertThat("Server Response status code should be " + statusCode, serverResponse.statusCode().value(), equalTo(statusCode));
                }))
                .verifyComplete();
    }

    static Stream<Arguments> successResponses() {
        return Stream.of(
                Arguments.of(RandomWordsResponse.builder()
                                    .word("unjinxed")
                                .build(),
                "unjinxed")
        );
    }

    static Stream<Arguments> exceptionResponses() {
        return Stream.of(
                Arguments.of(new HttpClientErrorException(HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new RequestEntityBuilderException("Failed to generate request entity"), HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
    }
}
