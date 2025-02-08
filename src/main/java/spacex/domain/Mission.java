package spacex.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Mission {
    private String name;
    private MissionStatus status;
    private Set<Rocket> rockets;

    public Mission(String name) {
        this.name = name;
        this.status = MissionStatus.SCHEDULED;
        this.rockets = new HashSet<>();
    }
}
