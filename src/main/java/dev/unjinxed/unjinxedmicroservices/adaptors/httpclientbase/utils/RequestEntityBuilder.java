package dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.utils;

import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Data
@Slf4j
public class RequestEntityBuilder {

    String sessionID;
    String domain;

    public  <T> RequestEntity<T> generateRequestEntity(@NotNull String resource, T body,
                                               @NotNull MultiValueMap<String,String> headers,
                                               @NotNull HttpMethod method) throws RequestEntityBuilderException {
        log.info("generateRequestEntity(): entering... resource[{}], body[{}], headers[{}], method[{}]", resource, body,headers, method);
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString(domain + resource)
                    .build()
                    .toUri();
            var requestEntity = new RequestEntity<>(body, headers, method, uri);
            log.info("generateRequestEntity(): requestEntity: {}", requestEntity);
            return requestEntity;
        } catch (RuntimeException runtimeException) {
            log.error("generateRequestEntity(): error generating request entity ", runtimeException);
            throw new RequestEntityBuilderException(runtimeException.getMessage());
        }
    }
}
