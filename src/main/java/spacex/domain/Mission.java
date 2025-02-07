package spacex.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Mission {
    private String id;
    private String name;
    private MissionStatus status;
    private Set<Rocket> rockets;

    public Mission(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.status = MissionStatus.SCHEDULED;
        this.rockets = new HashSet<>();
    }

    public void addRocket(Rocket rocket) {
        rockets.add(rocket);
    }
}
