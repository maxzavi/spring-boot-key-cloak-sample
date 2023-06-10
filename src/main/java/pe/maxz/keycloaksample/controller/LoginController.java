package pe.maxz.keycloaksample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import pe.maxz.keycloaksample.entity.Login;
import pe.maxz.keycloaksample.service.KeyCloakService;

@RestController
@RequestMapping("/api/v1/login")
@Tag(name = "Login", description = "Login API")
@Slf4j
public class LoginController {

    @Autowired
    KeyCloakService keyCloakService;

    @PostMapping (value = "", produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Access unauthorized")
    })
    public ResponseEntity<String> login(
            @RequestBody Login login){
        log.info("Login: {}", login);
        ResponseEntity<String> responseEntity =  keyCloakService.login(login);
        return  ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
}
