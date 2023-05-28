package dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries.impl;

import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.oxforddictionaries.OxfordDictionariesAdapter;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionariesResponse;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.services.oxforddictionaries.OxfordDictionariesService;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Mono;
@Service
public class OxfordDictionariesServiceImpl implements OxfordDictionariesService {
    OxfordDictionariesAdapter oxfordDictionariesAdapter;
    public OxfordDictionariesServiceImpl(@Autowired OxfordDictionariesAdapter oxfordDictionariesAdapter) {
        this.oxfordDictionariesAdapter = oxfordDictionariesAdapter;
    }
    public Mono<OxfordDictionariesResponse> getWordDefinition(String word) {
        return this.oxfordDictionariesAdapter.getWordDefinition(word);
    }

    public Mono<ServerResponse> getWordDefinitionServerResponse(String word) {
        return this.getWordDefinition(word)
                .map(oxfordDictionariesResponse -> ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Mono.just(oxfordDictionariesResponse), new ParameterizedTypeReference<>(){})
                )
                .onErrorResume(HttpClientErrorException.class, httpClientErrorException -> Mono.just(ServerResponse
                        .status(httpClientErrorException.getStatusCode().value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(httpClientErrorException.getResponseBodyAsString()), new ParameterizedTypeReference<Mono<Object>>(){}))
                ).onErrorResume(RequestEntityBuilderException.class, e -> Mono.just(ServerResponse
                        .status(500)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(e.getMessage()), new ParameterizedTypeReference<Mono<Object>>(){}))
                );
    }
}
