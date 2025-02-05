package service;

import domain.Rocket;
import domain.RocketStatus;
import lombok.AllArgsConstructor;
import repository.RocketRepository;

@AllArgsConstructor
public class RocketService {

    private final RocketRepository rocketRepository;

    public Rocket createRocket(String name) {
        Rocket rocket = new Rocket(name);

        rocketRepository.addRocket(rocket);

        return rocket;
    }

    public void updateRocketStatus(String rocketId, RocketStatus status) {
        Rocket rocket = rocketRepository.findById(rocketId)
                .orElseThrow(() -> new IllegalArgumentException("Rocket not found"));

        rocket.setStatus(status);
    }
}
