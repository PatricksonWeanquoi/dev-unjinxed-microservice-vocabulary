package dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.randomwordsadapter.impl;

import dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.HttpClientBase;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.adapters.randomwordsadapter.RandomWordsAdapter;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.randomwords.RandomWordsResponse;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Component
@Slf4j
@EqualsAndHashCode
public class RandomWordsAdapterImpl extends HttpClientBase implements RandomWordsAdapter {

    private String appHost;
    private String appKey;
    private MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();


    public RandomWordsAdapterImpl(@NotNull @Value("${X-RapidAPI-Host}")String appHost,
                                  @NotNull @Value("${X-RapidAPI-Key}") String appKey,
                                  @NotNull @Value("${X-RapidAPI-Domain}") String appDomain) {
        this.appHost = appHost;
        this.appKey = appKey;
        setDomain(appDomain);
        initializeHeaders();
    }


    private void initializeHeaders() {
        headers.add("X-RapidAPI-Host", this.appHost);
        headers.add("X-RapidAPI-Key", this.appKey);
    }

    public Mono<RandomWordsResponse> getRandomWord() {
        final String requestPath =  "/getRandom";
        log.info("getRandomWord(): entering... resource path: " + requestPath);
        try {
            RequestEntity<Void> requestEntity = this.createRequestEntity(requestPath,null, headers, HttpMethod.GET);
            ParameterizedTypeReference<String> returnType = new ParameterizedTypeReference<>() {};
            return this.triggerServiceCallOut(requestEntity, returnType)
                    .doOnSuccess(responseEntity -> log.info("getRandomWord(): response " + responseEntity))
                    .map(ResponseEntity::getBody)
                    .map(word -> RandomWordsResponse.builder().word(word).build());
        } catch (RequestEntityBuilderException requestEntityBuilderException) {
            log.error("getRandomWord(): requestEntityBuilderException " + requestEntityBuilderException.getMessage());
            return Mono.error(requestEntityBuilderException);
        }
    }

    public <T, U> Mono<ResponseEntity<U>> triggerServiceCallOut(RequestEntity<T> request, ParameterizedTypeReference<U> returnType) {
        return super.serviceCallOut(request, returnType);
    }

    public <T> RequestEntity<T> createRequestEntity(@NotNull String resource, T body,
                                                     @NotNull MultiValueMap<String,String> headers,
                                                     @NotNull HttpMethod method) throws RequestEntityBuilderException {
        return super.generateRequestEntity(resource, body, headers, method);
    }
}
