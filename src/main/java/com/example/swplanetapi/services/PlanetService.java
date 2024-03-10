package com.example.swplanetapi.services;

import com.example.swplanetapi.entities.Planet;
import com.example.swplanetapi.repositories.PlanetRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlanetService {
    private PlanetRepository repository;

    public PlanetService(PlanetRepository repository) {
        this.repository = repository;
    }

    public Planet create(Planet planet){
        return repository.save(planet);
    }

    public Optional<Planet> get(Long id){
        return repository.findById(id);
    }
}
