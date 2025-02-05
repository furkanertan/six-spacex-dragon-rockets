package repository;

import domain.Rocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RocketRepository {
    private final Map<String, Rocket> rockets = new HashMap<>();

    public void addRocket(Rocket rocket) {
        rockets.put(rocket.getId(), rocket);
    }

    public Optional<Rocket> findById(String id) {
        return Optional.ofNullable(rockets.get(id));
    }

    public Collection<Rocket> findAll() {
        return rockets.values();
    }
}
