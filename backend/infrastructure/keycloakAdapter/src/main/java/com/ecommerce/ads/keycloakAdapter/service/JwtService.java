package com.ecommerce.ads.keycloakAdapter.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URL;

@Service
public class JwtService {
    @Value("${keycloak.jwk-set-uri}")
    private String jwksUrl;

    public Jwk getJwk() throws Exception {
        URL url = new URL(jwksUrl);
        UrlJwkProvider provider = new UrlJwkProvider(url);
        // Retorna el primer JWK encontrado o filtra por ID si es necesario
        return provider.get(provider.getAll().get(0).getId());
    }
}