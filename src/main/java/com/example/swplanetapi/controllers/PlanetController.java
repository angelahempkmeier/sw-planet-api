package com.example.swplanetapi.controllers;

import com.example.swplanetapi.entities.Planet;
import com.example.swplanetapi.services.PlanetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/planets")
public class PlanetController {
    @Autowired
    private PlanetService service;

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody Planet planet){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(planet));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> get(@PathVariable("id") Long id){
        return service.get(id).map(planet -> ResponseEntity.ok(planet))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Planet> getByName(@PathVariable("name") String name){
        return service.getByName(name).map(planet -> ResponseEntity.ok(planet))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
