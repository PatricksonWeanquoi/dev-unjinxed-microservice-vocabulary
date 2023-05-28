package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.oxforddictionaries.OxfordDictionariesAdapter;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.*;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries.impl.OxfordDictionariesServiceImpl;
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

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
@DisplayName("Test-Case: Oxford Dictionaries Service")
class OxfordDictionariesServiceTest extends MockitoInit {
    @Mock
    OxfordDictionariesAdapter oxfordDictionariesAdapter;
    @InjectMocks
    OxfordDictionariesServiceImpl oxfordDictionariesService;

    @DisplayName("Get Word Definition Success Test")
    @ParameterizedTest
    @MethodSource("successResponses")
    void getWordDefinitionSuccessTest(OxfordDictionariesResponse oxfordDictionariesResponse, String word) {
        Mockito.when(oxfordDictionariesAdapter.getWordDefinition(word)).thenReturn(Mono.just(oxfordDictionariesResponse));
        Mono<OxfordDictionariesResponse> oxfordDictionariesResponseMono = oxfordDictionariesService.getWordDefinition(word);
        StepVerifier.create(oxfordDictionariesResponseMono)
                .assertNext((oxfordDictionariesResp -> {
                    assertThat("Oxford Dictionaries Response is not null", oxfordDictionariesResp, is(notNullValue()));
                    assertThat("Has word defined ", oxfordDictionariesResp.getWord(), is(word));
                    assertThat("Should have Results object", oxfordDictionariesResp.getResults(), is(notNullValue()));
                    assertThat("Should have lexicalEntries Object", oxfordDictionariesResp.getResults().get(0).getLexicalEntries().size(), greaterThan(0));
                    assertThat("Should have entries Object", oxfordDictionariesResp.getResults().get(0).getLexicalEntries().get(0).getEntries().size(), greaterThan(0));
                    assertThat("Should have senses Object", oxfordDictionariesResp.getResults().get(0).getLexicalEntries().get(0).getEntries().get(0).getSenses().size(), greaterThan(0));
                    assertThat("Should have definitions Object", oxfordDictionariesResp.getResults().get(0).getLexicalEntries().get(0).getEntries().get(0).getSenses().get(0).getDefinitions().size(), greaterThan(0));
                    assertThat("Should have examples Object", oxfordDictionariesResp.getResults().get(0).getLexicalEntries().get(0).getEntries().get(0).getSenses().get(0).getExamples().size(), greaterThan(0));
                }))
                .verifyComplete();
    }

    @DisplayName("Get Word Definition Success Server Response Test")
    @ParameterizedTest
    @MethodSource("successResponses")
    void getWordDefinitionSuccessServerResponseTest(OxfordDictionariesResponse oxfordDictionariesResponse, String word) {
        Mockito.when(oxfordDictionariesAdapter.getWordDefinition(word)).thenReturn(Mono.just(oxfordDictionariesResponse));
        Mono<ServerResponse> serverResponseMono = oxfordDictionariesService.getWordDefinitionServerResponse(word);
        StepVerifier.create(serverResponseMono)
                .assertNext((serverResponse -> {
                    assertThat("Server Response is not null", serverResponse, is(notNullValue()));
                    assertThat("Server Response status code should 200", serverResponse.statusCode().value(), equalTo(200));
                }))
                .verifyComplete();
    }

    @DisplayName("Get Word Definition Exception Server Response Test")
    @ParameterizedTest
    @MethodSource("exceptionResponses")
    void getWordDefinitionExceptionServerResponseTest(Throwable exception, Integer statusCode) {
        Mockito.when(oxfordDictionariesAdapter.getWordDefinition(anyString())).thenReturn(Mono.error(exception));
        Mono<ServerResponse> serverResponseMono = oxfordDictionariesService.getWordDefinitionServerResponse(anyString());
        StepVerifier.create(serverResponseMono)
                .assertNext((serverResponse -> {
                    assertThat("Server Response is not null", serverResponse, is(notNullValue()));
                    assertThat("Server Response status code should be " + statusCode, serverResponse.statusCode().value(), equalTo(statusCode));
                }))
                .verifyComplete();
    }

    static Stream<Arguments> successResponses() {
        return Stream.of(
                Arguments.of(OxfordDictionariesResponse.builder()
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
                                                        .build()))
                                        .build()))
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
