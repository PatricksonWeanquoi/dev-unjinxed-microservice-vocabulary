package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi.WordResponse;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface WordsAPIService {

    Mono<WordResponse> getRandomWord();
    Mono<WordResponse> getWordDefinitions(String word);
    Mono<WordResponse> getWordExamples(String word);
    Mono<WordResponse> getRandomWordDefinitions();
    Mono<ServerResponse> getRandomWordServerResponse();
    Mono<ServerResponse> getWordDefinitionsServerResponse(String word);
    Mono<ServerResponse> getWordExamplesServerResponse(String word);
    Mono<ServerResponse> getRandomWordDefinitionsServerResponse();
}
