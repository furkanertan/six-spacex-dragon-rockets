package repository;

import domain.Mission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MissionRepository {
    private final Map<String, Mission> missions = new HashMap<>();

    public void addMission(Mission mission) {
        missions.put(mission.getId(), mission);
    }

    public Optional<Mission> findById(String id) {
        return Optional.ofNullable(missions.get(id));
    }

    public Collection<Mission> findAll() {
        return missions.values();
    }
}
