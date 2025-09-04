package dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.wordsapiadapter.impl;

import dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.HttpClientBase;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.wordsapiadapter.WordsAPIAdapter;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.enums.WordEnum;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.wordsapi.WordResponse;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.logging.Level;

@Component
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class WordsAPIAdapterImpl extends HttpClientBase implements WordsAPIAdapter {

    private final String appHost;
    private final String appKey;
    private final MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();


    public WordsAPIAdapterImpl(@NotNull @Value("${rapidApi.wordsApi.xRapidAPIHost}")String appHost,
                               @NotNull @Value("${rapidApi.xRapidAPIKey}") String appKey,
                               @NotNull @Value("${rapidApi.wordsApi.xRapidAPIDomain}") String appDomain) {
        this.appHost = appHost;
        this.appKey = appKey;
        setDomain(appDomain);
        initializeHeaders();
    }


    private void initializeHeaders() {
        headers.add("X-RapidAPI-Host", this.appHost);
        headers.add("X-RapidAPI-Key", this.appKey);
    }

    public Mono<WordResponse> getWord(WordEnum wordEnum, String word) {
        final String requestPath =  "/words".concat(getPath(wordEnum, word));
        log.info("getWord(): entering... resource path: {}, action: {}", requestPath, wordEnum);
        try {
            RequestEntity<Void> requestEntity = this.createRequestEntity(requestPath,null, headers, HttpMethod.GET);
            ParameterizedTypeReference<WordResponse> returnType = new ParameterizedTypeReference<>() {};
            return this.triggerServiceCallOut(requestEntity, returnType)
                    .doOnSuccess(responseEntity -> log.info("getWord(): action: {}, response: {}", wordEnum, responseEntity))
                    .log("RandomWordsResponse error", Level.SEVERE, SignalType.ON_ERROR);
        } catch (RequestEntityBuilderException requestEntityBuilderException) {
            log.error("getRandomWord(): action: {}, requestEntityBuilderException: ", wordEnum, requestEntityBuilderException);
            return Mono.error(requestEntityBuilderException);
        }
    }

    public <T, U> Mono<U> triggerServiceCallOut(RequestEntity<T> request, ParameterizedTypeReference<U> returnType) {
        return super.serviceCallOut(request, returnType);
    }

    public <T> RequestEntity<T> createRequestEntity(@NotNull String resource, T body,
                                                     @NotNull MultiValueMap<String,String> headers,
                                                     @NotNull HttpMethod method) throws RequestEntityBuilderException {
        return super.generateRequestEntity(resource, body, headers, method);
    }

    private String getPath(WordEnum wordEnum, String word) {
        return switch (wordEnum) {
            case RANDOM -> "/?random=true";
            case EXAMPLES -> "/".concat(word).concat("/examples");
            case DEFINITIONS -> "/".concat(word).concat("/definitions");
        };
    }
}
