package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.impl;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.randomwordsadapter.RandomWordsAdapter;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.randomwords.RandomWordsResponse;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.randomwords.RandomWordsService;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class RandomWordsServiceImpl implements RandomWordsService {

    RandomWordsAdapter randomWordsAdapter;

    public RandomWordsServiceImpl(@Autowired RandomWordsAdapter randomWordsAdapter) {
        this.randomWordsAdapter = randomWordsAdapter;
    }
    public Mono<RandomWordsResponse> getRandomWord() {
        return this.randomWordsAdapter.getRandomWord();
    }
    public Mono<ServerResponse> getRandomWordServerResponse() {
        return this.getRandomWord()
                .map(randomWordsResponse -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(randomWordsResponse), new ParameterizedTypeReference<>(){}))
                .onErrorResume(HttpClientErrorException.class, httpClientErrorException -> Mono.just(ServerResponse
                        .status(httpClientErrorException.getStatusCode().value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(httpClientErrorException.getResponseBodyAsString()), new ParameterizedTypeReference<Mono<Object>>(){}))
                ).onErrorResume(RequestEntityBuilderException.class, e -> Mono.just(ServerResponse
                        .status(500)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(e.getMessage()), new ParameterizedTypeReference<Mono<Object>>(){})));
    }
}
