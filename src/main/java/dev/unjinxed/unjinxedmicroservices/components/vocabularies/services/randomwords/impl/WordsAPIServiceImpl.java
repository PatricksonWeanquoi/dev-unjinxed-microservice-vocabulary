package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.impl;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.wordsapiadapter.WordsAPIAdapter;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.enums.WordEnum;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi.WordResponse;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi.WordResult;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.WordsAPIService;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class WordsAPIServiceImpl implements WordsAPIService {

    private final WordsAPIAdapter randomWordsAdapter;
    @Value("${vocabulary.maxAttempts:10}")
    private int maxAttempts;


    public WordsAPIServiceImpl(@Autowired WordsAPIAdapter randomWordsAdapter) {
        this.randomWordsAdapter = randomWordsAdapter;
    }
    public Mono<WordResponse> getRandomWord() {
        return this.randomWordsAdapter.getWord(WordEnum.RANDOM, null);
    }

    public Mono<WordResponse> getWordDefinitions(String word) {
        return this.randomWordsAdapter.getWord(WordEnum.DEFINITIONS, word);
    }

    public Mono<WordResponse> getWordExamples(String word) {
        return this.randomWordsAdapter.getWord(WordEnum.EXAMPLES, word);
    }

    public Mono<WordResponse> getRandomWordDefinitions() {
        AtomicReference<WordResponse> wordAtomic = new AtomicReference<>(WordResponse.builder().build());
        return getRandomWord()
                .filter(wordResponse -> wordResponse.getWord().split(" ").length == 1 && !CollectionUtils.isEmpty(wordResponse.getResults()))
                .repeatWhenEmpty(maxAttempts,Flux::repeat)
                .flatMap(wordResponse -> {
                    final String word = wordResponse.getWord();
                    final List<WordResult> definitions = List.copyOf(wordResponse.getResults());
                    wordAtomic.get().setWord(word);
                    wordAtomic.get().setDefinitions(definitions);
                    return getWordExamples(word);
                })
                .map(wordResponse -> {
                    final List<String> examples = List.copyOf(wordResponse.getExamples());
                    wordAtomic.get().setExamples(examples);
                    return wordAtomic.get();
                });
    }

    public Mono<ServerResponse> getRandomWordServerResponse() {
        return convertToMonoServerResponse(getRandomWord());
    }

    public Mono<ServerResponse> getWordDefinitionsServerResponse(String word) {
        return convertToMonoServerResponse(getWordDefinitions(word));
    }

    public Mono<ServerResponse> getWordExamplesServerResponse(String word) {
        return convertToMonoServerResponse(getWordExamples(word));
    }

    public Mono<ServerResponse> getRandomWordDefinitionsServerResponse() {
        return convertToMonoServerResponse(getRandomWordDefinitions());
    }

    private Mono<ServerResponse> convertToMonoServerResponse(Mono<WordResponse> wordResponseMono) {
        return wordResponseMono
                .flatMap(randomWordsResponse -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(randomWordsResponse), WordResponse.class)
                )
                .onErrorResume(HttpClientErrorException.class, httpClientErrorException -> ServerResponse
                        .status(httpClientErrorException.getStatusCode().value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(httpClientErrorException.getResponseBodyAsString()), new ParameterizedTypeReference<Mono<Object>>(){})
                ).onErrorResume(RequestEntityBuilderException.class, e -> ServerResponse
                        .status(500)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(e.getMessage()), new ParameterizedTypeReference<Mono<Object>>(){})
                );
    }
}
