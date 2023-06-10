package pe.maxz.keycloaksample.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import pe.maxz.keycloaksample.entity.Login;
import pe.maxz.keycloaksample.entity.UserInfo;

@Service
@Slf4j
public class KeyCloakService {

    @Value("${keycloak.client-id}")
    String clientId;
    @Value("${keycloak.grant-type}")
    String grantType;
    @Value("${keycloak.realm-id}")
    String realmId;
    @Value("${keycloak.url-base}")
    String urlBase;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> login (Login login){
        log.info("Using: url-base: {} realm-id: {}", urlBase, realmId);
        log.info("Login username: {}", login.getUsername());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username",login.getUsername());
        map.add("password",login.getPassword());
        map.add("client_id",clientId);
        map.add("grant_type", grantType);
        map.add("scope", "openid");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        
        String uri= urlBase + "/realms/"+ realmId  +"/protocol/openid-connect/token";
        try {
            return restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        } catch(HttpStatusCodeException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(e.getStatusCode().value()).headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }

    private String getUserInfo(String token) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", token);

        String uri= urlBase + "/realms/"+ realmId  +"/protocol/openid-connect/userinfo";

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        return restTemplate.postForObject(uri, request, String.class);
    }
    public HttpStatus valid (String role, String token){
        try {
            String result = getUserInfo(token);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            UserInfo userInfo = objectMapper.readValue(result, UserInfo.class);
            log.info("UserInfo: {}", userInfo);
            boolean valid= userInfo.getRealm_access().getRoles().contains(role);
            if (!valid) return HttpStatus.FORBIDDEN;
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.UNAUTHORIZED;
        }
    }
}
