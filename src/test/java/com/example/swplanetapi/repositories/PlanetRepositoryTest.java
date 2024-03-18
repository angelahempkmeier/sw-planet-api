package com.example.swplanetapi.repositories;

import com.example.swplanetapi.entities.Planet;

import static com.example.swplanetapi.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.*;

import com.example.swplanetapi.utils.QueryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.swplanetapi.common.PlanetConstants.PLANET;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;
    @Autowired
    private TestEntityManager testEntityManager; //não faria sentido usar o planetRepository, já que ele é o alvo dos testes

    @AfterEach
    public void afterEach(){
        PLANET.setId(null);
    }

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet(){
        Planet planet = planetRepository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
    }

    @ParameterizedTest
    @MethodSource("providesInvalidPlanets")
    public void createPlanet_WithInvalidData_ThrowsException(Planet planet){
        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    private static Stream<Arguments> providesInvalidPlanets(){
        return Stream.of(
                Arguments.of(new Planet(null, "climate", "climate")),
                Arguments.of(new Planet("name", null, "climate")),
                Arguments.of(new Planet("name", "climate", null)),
                Arguments.of(new Planet(null, null, "climate")),
                Arguments.of(new Planet(null, "climate", null)),
                Arguments.of(new Planet("name", null, null)),
                Arguments.of(new Planet(null, null, null)),
                Arguments.of(new Planet("", "terrain", "climate")),
                Arguments.of(new Planet("name", "", "climate")),
                Arguments.of(new Planet("name", "terrain", "")),
                Arguments.of(new Planet("", "", "climate")),
                Arguments.of(new Planet("", "terrain", "")),
                Arguments.of(new Planet("name", "", "")),
                Arguments.of(new Planet("", "", ""))
                );
    }

    @Test
    public void createPlanet_WithExistingName_ThrowsException(){
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> planetOptional = planetRepository.findById(planet.getId());

        assertThat(planetOptional).isNotEmpty();
        assertThat(planetOptional.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsNotFound() {
        Optional<Planet> planetOptional = planetRepository.findById(1L);

        assertThat(planetOptional).isEmpty();
    }
    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> planetOptional = planetRepository.findByName(planet.getName());

        assertThat(planetOptional).isNotEmpty();
        assertThat(planetOptional.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsNotFound() {
        Optional<Planet> planetOptional = planetRepository.findByName("marte");

        assertThat(planetOptional).isEmpty();
    }

    @Sql(scripts = {"/import_planets.sql"})
    @Test
    public void listPlanets_ReturnsFilteredPlanets(){
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getTerrain(), TATOOINE.getClimate()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty();
        assertThat(responseWithoutFilters).hasSize(3);
        assertThat(responseWithFilters).isNotEmpty();
        assertThat(responseWithFilters).hasSize(1);
        assertThat(responseWithFilters.get(0)).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ReturnsNoPlanets(){
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());

        List<Planet> response = planetRepository.findAll(query);

        assertThat(response).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase(){
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(planet.getId());

        Planet removedPlanet = testEntityManager.find(Planet.class, planet.getId());
        assertThat(removedPlanet).isNull();
    }

    @Test
    public void removePlanet_WithUnexistingId_ThrowsException(){
        assertThatThrownBy(() -> planetRepository.deleteById(99L)).isInstanceOf(EmptyResultDataAccessException.class);
    }
}
