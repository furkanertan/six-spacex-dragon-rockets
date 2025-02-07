package spacex.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Rocket {
    private String id;
    private String name;
    private RocketStatus status;

    public Rocket(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.status = RocketStatus.ON_GROUND;
    }
}
