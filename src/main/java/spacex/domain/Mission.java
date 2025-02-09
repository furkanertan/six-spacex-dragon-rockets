package spacex.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Mission {
    private String name;
    private MissionStatus status;
    private List<Rocket> rockets;

    public Mission(String name) {
        this.name = name;
        this.status = MissionStatus.SCHEDULED;
        this.rockets = new ArrayList<>();
    }
}
