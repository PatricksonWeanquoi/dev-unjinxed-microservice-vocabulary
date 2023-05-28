package dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase;


import dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.utils.RequestEntityBuilder;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
public class HttpClientBase extends RequestEntityBuilder {
    @Qualifier("restTemplate")
    @Autowired
    RestTemplate restTemplate;

    public <T, U> Mono<ResponseEntity<U>> serviceCallOut(RequestEntity<T> request, ParameterizedTypeReference<U> returnType) {
        try {
            return Mono.just(this.restTemplate.exchange(request, returnType));
        } catch (HttpClientErrorException httpClientErrorException) {
            return Mono.error(httpClientErrorException);
        }
    }
}
