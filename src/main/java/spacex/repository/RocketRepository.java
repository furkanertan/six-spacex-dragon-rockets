package spacex.repository;

import spacex.domain.Rocket;

import java.util.HashMap;
import java.util.Map;

public class RocketRepository {

    private final Map<String, Rocket> rockets = new HashMap<>();

    public void addRocket(Rocket rocket) {
        rockets.put(rocket.getName(), rocket);
    }

    public Rocket getRocket(String name) {
        return rockets.get(name);
    }
}