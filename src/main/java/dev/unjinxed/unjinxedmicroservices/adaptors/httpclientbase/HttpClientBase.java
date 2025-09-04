package dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase;


import dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.utils.RequestEntityBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Slf4j
public class HttpClientBase extends RequestEntityBuilder {
    @Qualifier("webClient")
    @Autowired
    WebClient webClient;

    public <T, U> Mono<U> serviceCallOut(@NotNull RequestEntity<T> request, ParameterizedTypeReference<U> returnType) {
        try {
            return webClient
                    .method(request.getMethod())
                    .uri(request.getUrl())
                    .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
                    .retrieve()
                    .bodyToMono(returnType);

        } catch (HttpClientErrorException httpClientErrorException) {
            log.error("HttpClientErrorException - error ", httpClientErrorException);
            return Mono.error(httpClientErrorException);
        } catch (Exception e) {
            log.error("Exception - error ", e);
            return Mono.error(e);
        }
    }
}
