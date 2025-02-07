package spacex.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RocketStatus {
    ON_GROUND("On ground"),
    IN_SPACE("In space"),
    IN_REPAIR("In repair");

    private final String status;
}
