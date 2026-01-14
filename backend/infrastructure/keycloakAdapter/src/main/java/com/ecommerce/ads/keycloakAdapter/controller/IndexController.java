package com.ecommerce.ads.keycloakAdapter.controller;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.ecommerce.ads.keycloakAdapter.exception.BussinesRuleException;
import com.ecommerce.ads.keycloakAdapter.models.LoginRequest;
import com.ecommerce.ads.keycloakAdapter.models.UserRegistrationRequest;
import com.ecommerce.ads.keycloakAdapter.service.JwtService;
import com.ecommerce.ads.keycloakAdapter.service.KeycloakRestService;

import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/security")
//@CrossOrigin("*")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private KeycloakRestService restService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(@RequestHeader("Authorization") String authHeader) throws BussinesRuleException {
        try {
            DecodedJWT jwt = JWT.decode(authHeader.replace("Bearer", "").trim());
            Jwk jwk = jwtService.getJwk();
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);
            List<String> roles = ((List) jwt.getClaim("realm_access").asMap().get("roles"));
            Date expiryDate = jwt.getExpiresAt();
            if (expiryDate.before(new Date())) {
                throw new Exception("token is expired");
            }
            HashMap<String, Integer> roleMap = new HashMap<>();
            for (String str : roles) {
                roleMap.put(str, str.length());
            }
            return ResponseEntity.ok(roleMap);
        } catch (Exception e) {
            logger.error("exception : {} ", e.getMessage());
            throw new BussinesRuleException("01", e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/valid")
    public ResponseEntity<?> valid(@RequestHeader("Authorization") String authHeader) throws BussinesRuleException {
        try {
            restService.checkValidity(authHeader);
            return ResponseEntity.ok(new HashMap<String, String>() {{ put("is_valid", "true"); }});
        } catch (Exception e) {
            logger.error("token is not valid, exception : {} ", e.getMessage());
            throw new BussinesRuleException("is_valid", "False", HttpStatus.FORBIDDEN);
        }
    }

    // 👇 ESTE ES EL ÚNICO MÉTODO DE LOGIN QUE DEBE EXISTIR
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        String loginResponse = restService.login(username, password);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(loginResponse);
            
            if (responseJson.has("error")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }

            String accessToken = responseJson.get("access_token").asText();
            DecodedJWT jwt = JWT.decode(accessToken);
            String userId = jwt.getSubject();
            
            // Manejo seguro de claims opcionales
            String userName = jwt.getClaim("preferred_username").isNull() ? username : jwt.getClaim("preferred_username").asString();
            String email = jwt.getClaim("email").isNull() ? "no-email" : jwt.getClaim("email").asString();
            List<String> roles = ((List) jwt.getClaim("realm_access").asMap().get("roles"));

            Map<String, Object> result = new HashMap<>();
            result.put("token_response", mapper.readValue(loginResponse, Map.class));
            result.put("user_info", new HashMap<String, Object>() {{
                put("user_id", userId);
                put("username", userName);
                put("email", email);
                put("roles", roles);
            }});
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestParam(value = "refresh_token", name = "refresh_token") String refreshToken) throws BussinesRuleException {
        try {
            restService.logout(refreshToken);
            return ResponseEntity.ok(new HashMap<String, String>() {{ put("logout", "true"); }});
        } catch (Exception e) {
            logger.error("unable to logout, exception : {} ", e.getMessage());
            throw new BussinesRuleException("logout", "False", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refresh(@RequestParam(value = "refresh_token", name = "refresh_token") String refreshToken) throws BussinesRuleException {
        try {
            return ResponseEntity.ok(restService.refresh(refreshToken));
        } catch (Exception e) {
            logger.error("unable to refresh, exception : {} ", e.getMessage());
            throw new BussinesRuleException("refresh", "False", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }
            restService.setKeycloakBaseUri("http://localhost:8091");
            restService.createUser(request.getUsername(), request.getEmail(), request.getFirstName(), request.getLastName(), request.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registering user: " + e.getMessage());
        }
    }
}