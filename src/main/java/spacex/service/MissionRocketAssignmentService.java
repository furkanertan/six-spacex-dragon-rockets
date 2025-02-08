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

import java.util.List;

import static spacex.constant.ErrorMessages.*;

@AllArgsConstructor
public class MissionRocketAssignmentService {

    private final RocketRepository rocketRepository;
    private final MissionRepository missionRepository;

    // Add a new rocket
    public void addRocket(Rocket rocket) throws SpaceXException {
        if (rocketRepository.getRocket(rocket.getName()) != null) {
            throw new SpaceXException(ROCKET_ALREADY_EXISTS);
        }

        rocketRepository.addRocket(rocket);
    }

    // Add a new mission
    public void addMission(Mission mission) throws SpaceXException {
        if (missionRepository.getMission(mission.getName()) != null) {
            throw new SpaceXException(MISSION_ALREADY_EXISTS);
        }

        missionRepository.addMission(mission);
    }

    // Assign a rocket to a mission
    public void assignRocketToMission(String rocketName, String missionName) throws SpaceXException {
        Rocket rocket = getRocketOrThrow(rocketName);
        Mission mission = getMissionOrThrow(missionName);

        validateRocketAssignment(rocket, mission);

        rocket.setStatus(RocketStatus.IN_SPACE);
        mission.getRockets().add(rocket);
        updateMissionStatus(mission);
    }

    // Assign multiple rockets to a mission
    public void assignRocketsToMission(List<String> rocketNames, String missionName) throws SpaceXException {
        for (String rocketName : rocketNames) {
            assignRocketToMission(rocketName, missionName);
        }
    }

    // Change the status of a rocket
    public void changeRocketStatus(String rocketName, RocketStatus newStatus) throws SpaceXException {
        Rocket rocket = getRocketOrThrow(rocketName);

        validateRocketStatusChange(rocket, newStatus);

        rocket.setStatus(newStatus);

        // Update the mission status if the rocket is assigned to a mission
        missionRepository.getAllMissions().values().stream()
                .filter(mission -> mission.getRockets().contains(rocket))
                .findFirst()
                .ifPresent(this::updateMissionStatus);
    }

    // Change the status of a mission
    public void changeMissionStatus(String missionName, MissionStatus newStatus) throws SpaceXException {
        Mission mission = getMissionOrThrow(missionName);

        validateMissionStatusChange(mission, newStatus);

        mission.setStatus(newStatus);
    }

    // Helper method to get a rocket or throw an exception if not found
    private Rocket getRocketOrThrow(String rocketName) throws SpaceXException {
        Rocket rocket = rocketRepository.getRocket(rocketName);
        if (rocket == null) {
            throw new SpaceXException(ROCKET_NOT_FOUND);
        }
        return rocket;
    }

    // Helper method to get a mission or throw an exception if not found
    private Mission getMissionOrThrow(String missionName) throws SpaceXException {
        Mission mission = missionRepository.getMission(missionName);
        if (mission == null) {
            throw new SpaceXException(MISSION_NOT_FOUND);
        }
        return mission;
    }

    // Validate rocket assignment
    private void validateRocketAssignment(Rocket rocket, Mission mission) throws SpaceXException {
        if (!RocketStatus.ON_GROUND.equals(rocket.getStatus())) {
            throw new SpaceXException(ROCKET_NOT_AVAILABLE);
        }
        if (MissionStatus.ENDED.equals(mission.getStatus())) {
            throw new SpaceXException(MISSION_NOT_AVAILABLE);
        }
    }

    // Validate rocket status change
    private void validateRocketStatusChange(Rocket rocket, RocketStatus newStatus) throws SpaceXException {
        if (RocketStatus.IN_SPACE.equals(newStatus) && !isRocketAssignedToMission(rocket)) {
            throw new SpaceXException(ErrorMessages.ROCKET_CANNOT_BE_IN_SPACE_WITHOUT_MISSION);
        }
        if (RocketStatus.ON_GROUND.equals(newStatus) && isRocketAssignedToMission(rocket)) {
            throw new SpaceXException(ErrorMessages.ROCKET_CANNOT_BE_ON_GROUND);
        }
    }

    // Validate mission status change
    private void validateMissionStatusChange(Mission mission, MissionStatus newStatus) throws SpaceXException {
        if (MissionStatus.ENDED.equals(mission.getStatus())) {
            throw new SpaceXException(ErrorMessages.MISSION_CANNOT_BE_UPDATED_AFTER_ENDED);
        }
        if (MissionStatus.SCHEDULED.equals(mission.getStatus())) {
            throw new SpaceXException(ErrorMessages.MISSION_CANNOT_BE_UPDATED_AFTER_SCHEDULED);
        }
        if (MissionStatus.SCHEDULED.equals(newStatus)) {
            throw new SpaceXException(ErrorMessages.MISSION_CANNOT_BE_RESCHEDULED);
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
            mission.setStatus(MissionStatus.SCHEDULED);
        } else if (mission.getRockets().stream().anyMatch(rocket -> rocket.getStatus() == RocketStatus.IN_REPAIR)) {
            mission.setStatus(MissionStatus.PENDING);
        } else {
            mission.setStatus(MissionStatus.IN_PROGRESS);
        }
    }

    // Get a summary of missions, ordered by the number of rockets assigned
    public List<Mission> getMissionSummary() {
        return missionRepository.getAllMissions().values().stream()
                .sorted((m1, m2) -> {
                    int rocketCountComparison = Integer.compare(m2.getRockets().size(), m1.getRockets().size());
                    return rocketCountComparison != 0 ? rocketCountComparison : m2.getName().compareTo(m1.getName());
                })
                .toList();
    }
}