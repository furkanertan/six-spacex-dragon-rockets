package spacex.service;

import lombok.AllArgsConstructor;
import spacex.constant.ErrorMessages;
import spacex.domain.Mission;
import spacex.domain.MissionStatus;
import spacex.domain.Rocket;
import spacex.domain.RocketStatus;
import spacex.exception.SpaceXException;
import spacex.repository.MissionRepository;
import spacex.repository.RocketRepository;
import spacex.util.MissionSummaryFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static spacex.constant.ErrorMessages.*;

@AllArgsConstructor
public class MissionRocketAssignmentService {

    private final RocketRepository rocketRepository;
    private final MissionRepository missionRepository;

    public void addRocket(Rocket rocket) throws SpaceXException {
        if (rocketRepository.getRocket(rocket.getName()) != null) {
            throw new SpaceXException(ROCKET_ALREADY_EXISTS);
        }

        rocketRepository.addRocket(rocket);
    }

    public void addMission(Mission mission) throws SpaceXException {
        if (missionRepository.getMission(mission.getName()) != null) {
            throw new SpaceXException(MISSION_ALREADY_EXISTS);
        }

        missionRepository.addMission(mission);
    }

    public void assignRocketToMission(String rocketName, String missionName) throws SpaceXException {
        Rocket rocket = getRocketOrThrow(rocketName);
        Mission mission = getMissionOrThrow(missionName);

        validateMissionForRocketAssignment(mission);
        Mission currentMission = findMissionForRocket(rocket);

        validateRocketForMissionAssignment(rocket, currentMission);

        if (currentMission != null) {
            currentMission.getRockets().remove(rocket);
            updateMissionStatus(currentMission);
        } else {
            rocket.setStatus(RocketStatus.IN_SPACE);
        }

        mission.getRockets().add(rocket);
        updateMissionStatus(mission);
    }

    public void assignRocketsToMission(List<String> rocketNames, String missionName) throws SpaceXException {
        for (String rocketName : rocketNames) {
            assignRocketToMission(rocketName, missionName);
        }
    }

    public void changeRocketStatus(String rocketName, RocketStatus newStatus) throws SpaceXException {
        Rocket rocket = getRocketOrThrow(rocketName);
        Mission currentMission = findMissionForRocket(rocket);

        validateRocketStatusChange(rocket, newStatus);

        rocket.setStatus(newStatus);

        if (currentMission != null) {
            if (RocketStatus.ON_GROUND.equals(newStatus)) {
                currentMission.getRockets().remove(rocket);
            }
            updateMissionStatus(currentMission);
        }
    }

    private Rocket getRocketOrThrow(String rocketName) throws SpaceXException {
        return Optional.ofNullable(rocketRepository.getRocket(rocketName))
                .orElseThrow(() -> new SpaceXException(ROCKET_NOT_FOUND));
    }

    private Mission getMissionOrThrow(String missionName) throws SpaceXException {
        return Optional.ofNullable(missionRepository.getMission(missionName))
                .orElseThrow(() -> new SpaceXException(MISSION_NOT_FOUND));
    }

    private Mission findMissionForRocket(Rocket rocket) {
        return missionRepository.getAllMissions().values().stream()
                .filter(mission -> mission.getRockets().contains(rocket))
                .findFirst()
                .orElse(null);
    }

    // Validate mission for rocket assignment
    private void validateMissionForRocketAssignment(Mission mission) throws SpaceXException {
        if (MissionStatus.ENDED.equals(mission.getStatus())) {
            throw new SpaceXException(MISSION_NOT_AVAILABLE);
        }
    }

    // Validate rocket for mission assignment
    private void validateRocketForMissionAssignment(Rocket rocket, Mission currentMission) throws SpaceXException {
        if (!RocketStatus.ON_GROUND.equals(rocket.getStatus()) && currentMission == null) {
            throw new SpaceXException(ROCKET_NOT_AVAILABLE);
        }
    }

    // Validate rocket status change
    private void validateRocketStatusChange(Rocket rocket, RocketStatus newStatus) throws SpaceXException {
        if ((RocketStatus.IN_SPACE.equals(newStatus) || RocketStatus.IN_REPAIR.equals(newStatus)) && !isRocketAssignedToMission(rocket)) {
            throw new SpaceXException(ErrorMessages.ROCKET_CANNOT_BE_IN_SPACE_OR_IN_REPAIR_WITHOUT_MISSION);
        }
    }

    // Check if a rocket is assigned to any mission
    private boolean isRocketAssignedToMission(Rocket rocket) {
        return missionRepository.getAllMissions().values().stream()
                .anyMatch(mission -> mission.getRockets().contains(rocket));
    }

    // Update the mission status based on the status of its rockets
    private void updateMissionStatus(Mission mission) {
        if (mission.getRockets().isEmpty()) {
            mission.setStatus(MissionStatus.ENDED);

        } else if (mission.getRockets().stream().anyMatch(rocket -> rocket.getStatus() == RocketStatus.IN_REPAIR)) {
            mission.setStatus(MissionStatus.PENDING);

        } else {
            mission.setStatus(MissionStatus.IN_PROGRESS);
        }
    }

    public String getMissionSummary() {
        List<Mission> sortedMissions = missionRepository.getAllMissions().values().stream()
                .sorted(Comparator.comparingInt((Mission m) -> -m.getRockets().size())
                        .thenComparing(Mission::getName, Comparator.reverseOrder()))
                .toList();

        return MissionSummaryFormatter.formatMissions(sortedMissions);
    }
}