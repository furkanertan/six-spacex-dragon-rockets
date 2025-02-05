package domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
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
