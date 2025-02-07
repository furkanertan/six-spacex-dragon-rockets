package spacex.repository;

import spacex.domain.Mission;

import java.util.HashMap;
import java.util.Map;

public class MissionRepository {

    private final Map<String, Mission> missions = new HashMap<>();

    public void addMission(Mission mission) {
        missions.put(mission.getName(), mission);
    }

    public Mission getMission(String name) {
        return missions.get(name);
    }

    public Map<String, Mission> getAllMissions() {
        return missions;
    }
}