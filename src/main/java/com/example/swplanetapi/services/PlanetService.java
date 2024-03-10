package com.example.swplanetapi.services;

import com.example.swplanetapi.entities.Planet;
import com.example.swplanetapi.repositories.PlanetRepository;
import com.example.swplanetapi.utils.QueryBuilder;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<Planet> getByName(String name) {
        return repository.findByName(name);
    }

    public List<Planet> list(String terrain, String climate) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(climate, terrain));
        return repository.findAll(query);
    }

    public void remove(Long id) {
        repository.deleteById(id);
    }
}
