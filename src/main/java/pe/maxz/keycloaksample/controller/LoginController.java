package pe.maxz.keycloaksample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import pe.maxz.keycloaksample.entity.Login;
import pe.maxz.keycloaksample.service.KeyCloakService;

@RestController
@RequestMapping("/api/v1/login")
@Tag(name = "Login", description = "Login API")

public class LoginController {

    private final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    KeyCloakService keyCloakService;

    @PostMapping (value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody Login login){
        LOG.info("Login: {}", login);
        ResponseEntity<String> responseEntity =  keyCloakService.login(login);
        return  ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
}
