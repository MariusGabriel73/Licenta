package com.mycompany.myapp.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class DatabaseCheckResource {

    @Autowired
    private DatabaseClient databaseClient;

    @GetMapping("/db-check")
    public Mono<String> checkDatabaseConnection() {
        return databaseClient
            .sql("SELECT 1")
            .map((row, metadata) -> row.get(0, Integer.class))
            .one()
            .map(result -> "Conexiune reușită cu baza de date! Rezultat: " + result)
            .onErrorResume(e -> Mono.just("Eroare conexiune DB: " + e.getMessage()));
    }
}
