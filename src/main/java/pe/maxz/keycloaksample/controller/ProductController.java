package pe.maxz.keycloaksample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.maxz.keycloaksample.entity.Product;
import pe.maxz.keycloaksample.service.KeyCloakService;
import pe.maxz.keycloaksample.service.ProductService;

@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product", description = "Product API endpoint")

public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    KeyCloakService keyCloakService;

    @GetMapping("/")
    public ResponseEntity<List<Product>> getAll(
            @RequestHeader(value = "Authorization", required = false) String token) {
        var resulAuth = keyCloakService.valid("USER", token);
        if (resulAuth != HttpStatus.OK)
            return new ResponseEntity<>(resulAuth);
        return ResponseEntity.ok(productService.getAll());
    }

    @PostMapping(value = "/")
    public ResponseEntity<Product> create(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Product product) {
        var resultAuth = keyCloakService.valid("ADMIN", token);
        if (!resultAuth.equals(HttpStatus.OK))
            return new ResponseEntity<>(resultAuth);
        return new ResponseEntity<Product>(productService.create(product), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Access unauthorized"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> get(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable int id) {
        var resultAuth = keyCloakService.valid("USER", token);
        if (!resultAuth.equals(HttpStatus.OK))
            return new ResponseEntity<>(resultAuth);
        var product = productService.get(id);
        if (product == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable int id) {
        var resultAuth = keyCloakService.valid("ADMIN", token);
        if (!resultAuth.equals(HttpStatus.OK))
            return new ResponseEntity<>(resultAuth);
        var result = productService.delete(id);
        if (!result)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable int id,
            @RequestBody Product product) {
        var resultAuth = keyCloakService.valid("ADMIN", token);
        if (!resultAuth.equals(HttpStatus.OK))
            return new ResponseEntity<>(resultAuth);
        product.setId(id);
        var result = productService.update(product);
        if (result == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }
}
