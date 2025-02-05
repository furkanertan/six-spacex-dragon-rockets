package service;

import domain.Mission;
import domain.MissionStatus;
import domain.RocketStatus;
import lombok.AllArgsConstructor;
import repository.MissionRepository;

import java.util.*;

@AllArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;

    private final Map<String, List<String>> missionAssignments = new HashMap<>();

    public Mission createMission(String name) {
        Mission mission = new Mission(name);
        missionRepository.addMission(mission);
        return mission;
    }

    public void assignRocketToMission(String missionId, String rocketId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found"));

        missionAssignments.computeIfAbsent(missionId, k -> new ArrayList<>()).add(rocketId);
        updateMissionStatus(missionId);
    }

    private void updateMissionStatus(String missionId) {
        List<String> assignedRockets = missionAssignments.getOrDefault(missionId, Collections.emptyList());

        if (assignedRockets.isEmpty()) {
            missionRepository.findById(missionId).ifPresent(m -> m.setStatus(MissionStatus.SCHEDULED));
        } else {
            boolean hasRepairingRocket = assignedRockets.stream()
                    .anyMatch(RocketStatus.IN_REPAIR::equals);
            missionRepository.findById(missionId).ifPresent(m ->
                    m.setStatus(hasRepairingRocket ? MissionStatus.PENDING : MissionStatus.IN_PROGRESS));
        }
    }

    public List<String> getRocketsForMission(String missionId) {
        return missionAssignments.getOrDefault(missionId, Collections.emptyList());
    }
}
