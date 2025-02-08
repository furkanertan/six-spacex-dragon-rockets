package spacex.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rocket {
    private String name;
    private RocketStatus status;

    public Rocket(String name) {
        this.name = name;
        this.status = RocketStatus.ON_GROUND;
    }
}
