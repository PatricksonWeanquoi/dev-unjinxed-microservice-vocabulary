package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.vocabularyconstruct;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.*;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.randomwords.RandomWordsResponse;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.vocabularycontruct.WordDefinition;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries.OxfordDictionariesService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.RandomWordsService;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.vocabularyconstruct.impl.VocabularyConstructServiceImpl;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import dev.unjinxed.unjinxedmicroservices.utils.MockitoInit;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.stream.Stream;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
@DisplayName("Test-Case: Vocabulary Construct Service")
@Slf4j
class VocabularyConstructServiceTest extends MockitoInit {
    @Mock
    RandomWordsService randomWordsService;
    @Mock
    OxfordDictionariesService oxfordDictionariesService;
    @InjectMocks
    VocabularyConstructServiceImpl vocabularyConstructService;

    @DisplayName("Get Random Word Definition Success Test")
    @ParameterizedTest
    @MethodSource("successResponses")
    void getRandomWordDefinitionSuccessTest(OxfordDictionariesResponse oxfordDictionariesResponse,
                                            RandomWordsResponse randomWordsResponse, String word) {
        Mockito.when(oxfordDictionariesService.getWordDefinition(word)).thenReturn(Mono.just(oxfordDictionariesResponse));
        Mockito.when(randomWordsService.getRandomWord()).thenReturn(Mono.just(randomWordsResponse));
        Mono<WordDefinition> wordDefinitionMono = vocabularyConstructService.getRandomWordDefinition();
        StepVerifier.create(wordDefinitionMono)
                .assertNext(wordDefinition -> {
                    log.info(String.valueOf(wordDefinition));
                    assertThat("Word definition object is not null", wordDefinition, notNullValue());
                    assertThat("Word definition has word " + word, wordDefinition.getWord(), equalTo(word));
                    assertThat("Word definition has definitions", wordDefinition.getDefinitions().size(), greaterThan(0));
                    assertThat("Word definition has examples", wordDefinition.getExamples().size(), greaterThan(0));

                })
                .verifyComplete();
    }

    @DisplayName("Get Random Word Definition Success Server Response Test")
    @ParameterizedTest
    @MethodSource("successResponses")
    void getRandomWordDefinitionSuccessServerResponseTest(OxfordDictionariesResponse oxfordDictionariesResponse,
                                            RandomWordsResponse randomWordsResponse, String word) {
        Mockito.when(oxfordDictionariesService.getWordDefinition(word)).thenReturn(Mono.just(oxfordDictionariesResponse));
        Mockito.when(randomWordsService.getRandomWord()).thenReturn(Mono.just(randomWordsResponse));
        Mono<ServerResponse> serverResponseMono = vocabularyConstructService.getRandomWordDefinitionServerResponse();
        StepVerifier.create(serverResponseMono)
                .assertNext(serverResponse  -> {
                    assertThat("Server Response object is not null", serverResponse, notNullValue());
                    assertThat("Server Response status code should be 200", serverResponse.statusCode().value(), equalTo(200));
                })
                .verifyComplete();
    }

    @DisplayName("Get Random Word Definition Null Word Definition Composed Exception")
    @ParameterizedTest
    @MethodSource("successResponses")
    void getRandomWordDefinitionNullWordDefinitionExceptionTest(OxfordDictionariesResponse oxfordDictionariesResponse,
                                                          RandomWordsResponse randomWordsResponse, String word) {
        oxfordDictionariesResponse.getResults().get(0).getLexicalEntries().get(0).setLexicalCategory(null);
        Mockito.when(oxfordDictionariesService.getWordDefinition(word)).thenReturn(Mono.just(oxfordDictionariesResponse));
        Mockito.when(randomWordsService.getRandomWord()).thenReturn(Mono.just(randomWordsResponse));
        Mono<ServerResponse> serverResponseMono = vocabularyConstructService.getRandomWordDefinitionServerResponse();
        StepVerifier.create(serverResponseMono)
                .assertNext(serverResponse  -> {
                    assertThat("Server Response object is not null", serverResponse, notNullValue());
                    assertThat("Server Response status code should be 500", serverResponse.statusCode().value(), equalTo(500));
                })
                .verifyComplete();
    }

    @DisplayName("Get Random Word Definition Exception Server Response Test")
    @ParameterizedTest
    @MethodSource("exceptionResponses")
    void getRandomWordDefinitionExceptionServerResponseTest(Throwable exception, Integer statusCode) {
        Mockito.when(oxfordDictionariesService.getWordDefinition(anyString())).thenReturn(Mono.error(exception));
        Mockito.when(randomWordsService.getRandomWord()).thenReturn(Mono.error(exception));
        Mono<ServerResponse> serverResponseMono = vocabularyConstructService.getRandomWordDefinitionServerResponse();
        StepVerifier.create(serverResponseMono)
                .assertNext(serverResponse  -> {
                    assertThat("Server Response object is not null", serverResponse, notNullValue());
                    assertThat("Server Response status code should be " + statusCode, serverResponse.statusCode().value(), equalTo(statusCode));
                })
                .verifyComplete();
    }

    static Stream<Arguments> successResponses() {
        return Stream.of(
                Arguments.of(
                OxfordDictionariesResponse.builder()
                                .id("unjinxed")
                                .results(List.of(OxfordDictionariesResults.builder()
                                        .id("unjinxed")
                                        .lexicalEntries(List.of(OxfordDictionariesLexicalEntry.builder()
                                                .entries(List.of(OxfordDictionariesEntry.builder()
                                                        .senses(List.of(OxfordDictionariesSenses.builder()
                                                                .definitions(List.of("Demo app", "Best app"))
                                                                .examples(List.of(OxfordDictionariesExample.builder()
                                                                        .text("unjinxed is a popular app")
                                                                        .build()))
                                                                .build()))
                                                        .build()))
                                                        .lexicalCategory(OxfordDictionarieslexicalCategory.builder()
                                                                .text("Noun")
                                                                .build())
                                                .build()))
                                        .build()))
                                .word("unjinxed")
                                .build(),
                        RandomWordsResponse.builder()
                                .word("unjinxed")
                                .build(),
                        "unjinxed")
        );
    }

    static Stream<Arguments> exceptionResponses() {
        return Stream.of(
                Arguments.of(new HttpClientErrorException(HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new RequestEntityBuilderException("Failed to generate request entity"), HttpStatus.INTERNAL_SERVER_ERROR.value()),
                Arguments.of(new RuntimeException("Could not compose word definition"), HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
    }

}
