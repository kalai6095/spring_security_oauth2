package com.alainelkhoury.resourceserver.services;

import com.alainelkhoury.resourceserver.dto.UserInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LocalAuthenticationService implements AuthenticationService {

    private final String introspectionUri;

    public LocalAuthenticationService(@Value("${auth.introspection-uri}") String introspectionUri) {
        this.introspectionUri = introspectionUri;
    }

    @Override
    public UserInfoDTO introspect(String token) {

        final String PATH = "/oauth/check_token";

        String rawResponse = WebClient.create(this.introspectionUri)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH)
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    throw new OAuth2IntrospectionException("Unauthorized! possible invalid token");
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    throw new OAuth2IntrospectionException("Unauthorized");
                })
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UserInfoDTO userInfoDTO = objectMapper.readValue(rawResponse, UserInfoDTO.class);
            return userInfoDTO;
        } catch (JsonProcessingException e) {
            throw new OAuth2IntrospectionException("Unauthorized");
        }
    }
}
