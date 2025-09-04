package dev.unjinxed.unjinxedmicroservices.adapters.httpclientbase.utils;

import dev.unjinxed.unjinxedmicroservices.adaptors.httpclientbase.utils.RequestEntityBuilder;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.oxforddictionaries.OxfordDictionariesExample;
import dev.unjinxed.unjinxedmicroservices.components.vocabularies.models.vocabularycontruct.WordDefinition;
import dev.unjinxed.unjinxedmicroservices.exceptions.RequestEntityBuilderException;
import dev.unjinxed.unjinxedmicroservices.utils.MockitoInit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
@DisplayName("Test-Case: Request Entity Builder")
class RequestEntityBuilderTest extends MockitoInit {
    @Tag("request-entity-builder")
    @DisplayName("Check Request Body")
    @ParameterizedTest
    @MethodSource("requests")
     void generateRequestEntityCheckBodyTest(Object requestBody,
                                            String resource,
                                            MultiValueMap<String, String> headers,
                                            ParameterizedTypeReference<Object> returnType,
                                            HttpMethod method) throws RequestEntityBuilderException {
        RequestEntityBuilder requestEntityBuilder = new RequestEntityBuilder();
        String domain = "http://unjinxed.dev";
        requestEntityBuilder.setDomain(domain);
        requestEntityBuilder.setSessionID(UUID.randomUUID().toString());
        RequestEntity<Object> requestEntity = requestEntityBuilder.generateRequestEntity(resource, requestBody, headers, method);
        String url = domain + resource;
        assertThat("Url should Match", url, equalTo(requestEntity.getUrl().toString()));
        assertThat("Http Method should match", method, equalTo(requestEntity.getMethod()));
        assertThat("Request Body should match", requestBody, equalTo(requestEntity.getBody()));
        assertThat("Request Body type should be same to returnType", returnType.getType(), equalTo(requestEntity.getBody().getClass()));
    }

    static Stream<Arguments> requests() {
        return Stream.of(
                Arguments.of(
            WordDefinition.builder()
                                .word("nothing")
                                .definitions(List.of("does not exist"))
                                .examples(List.of(OxfordDictionariesExample.builder()
                                        .text("noting is happening here")
                                        .build()))
                                .build(),
                        "/definition",
                        new LinkedMultiValueMap<>(),
                        new ParameterizedTypeReference<WordDefinition>() {},
                        HttpMethod.GET)
        );
    }
}
