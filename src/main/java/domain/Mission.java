package domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Mission {
    private String id;
    private String name;
    private MissionStatus status;

    public Mission(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.status = MissionStatus.SCHEDULED;
    }
}
