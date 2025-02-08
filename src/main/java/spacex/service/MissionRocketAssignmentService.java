package spacex.service;

import lombok.AllArgsConstructor;
import spacex.constant.ErrorMessages;
import spacex.domain.Mission;
import spacex.domain.MissionStatus;
import spacex.domain.Rocket;
import spacex.domain.RocketStatus;
import spacex.exception.InvalidEntryException;
import spacex.exception.MissionStatusException;
import spacex.exception.RocketStatusException;
import spacex.repository.MissionRepository;
import spacex.repository.RocketRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static spacex.constant.ErrorMessages.*;

@AllArgsConstructor
public class MissionRocketAssignmentService {

    private RocketRepository rocketRepository;
    private MissionRepository missionRepository;

    public void addRocket(Rocket rocket) throws InvalidEntryException {
        if (rocketRepository.getRocket(rocket.getName()) != null) {
            throw new InvalidEntryException(ROCKET_ALREADY_EXISTS);
        }

        rocketRepository.addRocket(rocket);
    }

    public void addMission(Mission mission) throws InvalidEntryException {
        if (missionRepository.getMission(mission.getName()) != null) {
            throw new InvalidEntryException(MISSION_ALREADY_EXISTS);
        }

        missionRepository.addMission(mission);
    }

    public void assignRocketToMission(String rocketName, String missionName) throws RocketStatusException, MissionStatusException {
        Rocket rocket = rocketRepository.getRocket(rocketName);
        Mission mission = missionRepository.getMission(missionName);

        validateRocket(rocket);
        validateMission(mission);

        rocket.setStatus(RocketStatus.IN_SPACE);
        mission.getRockets().add(rocket);
        updateMissionStatus(mission);
    }

    private void validateRocket(Rocket rocket) throws RocketStatusException {
        if (rocket == null) {
            throw new RocketStatusException(ROCKET_NOT_FOUND);
        }

        if (!RocketStatus.ON_GROUND.equals(rocket.getStatus())) {
            throw new RocketStatusException(ROCKET_NOT_AVAILABLE);
        }
    }

    private void validateMission(Mission mission) throws MissionStatusException {
        if (mission == null) {
            throw new MissionStatusException(MISSION_NOT_FOUND);
        }

        if (MissionStatus.ENDED.equals(mission.getStatus())) {
            throw new MissionStatusException(MISSION_NOT_AVAILABLE);
        }
    }

    public void assignRocketsToMission(List<String> rocketNames, String missionName) throws RocketStatusException, MissionStatusException {
        for (String rocketName : rocketNames) {
            assignRocketToMission(rocketName, missionName);
        }
    }

    public void changeRocketStatus(String rocketName, RocketStatus status) {
        Rocket rocket = rocketRepository.getRocket(rocketName);
        if (rocket != null) {
            rocket.setStatus(status);

            // Find the mission this rocket is assigned to and update its status
            missionRepository.getAllMissions().values().stream()
                    .filter(mission -> mission.getRockets().contains(rocket))
                    .findFirst()
                    .ifPresent(this::updateMissionStatus);
        }
    }

    public void changeMissionStatus(String missionName, MissionStatus status) throws MissionStatusException {
        Mission mission = missionRepository.getMission(missionName);

        if (mission == null) {
            throw new MissionStatusException(ErrorMessages.MISSION_NOT_FOUND);
        }

        validateMissionStatusChange(mission, status);

        mission.setStatus(status);
    }

    private void validateMissionStatusChange(Mission mission, MissionStatus newStatus) throws MissionStatusException {
        switch (newStatus) {
            case ENDED:
                validateEndedStatus(mission);
                break;
            case PENDING:
                validatePendingStatus(mission);
                break;
            case SCHEDULED:
                validateScheduledStatus(mission);
                break;
            case IN_PROGRESS:
                validateInProgressStatus(mission);
                break;
            default:
                break;
        }
    }

    private void validateEndedStatus(Mission mission) throws MissionStatusException {
        if (!mission.getRockets().isEmpty()) {
            throw new MissionStatusException(ErrorMessages.MISSION_CANNOT_END);
        }
    }

    private void validatePendingStatus(Mission mission) throws MissionStatusException {
        if (mission.getRockets().isEmpty() || mission.getRockets().stream()
                .noneMatch(rocket -> rocket.getStatus() == RocketStatus.IN_REPAIR)) {
            throw new MissionStatusException(ErrorMessages.MISSION_CANNOT_BE_PENDING);
        }
    }

    private void validateScheduledStatus(Mission mission) throws MissionStatusException {
        if (!mission.getRockets().isEmpty()) {
            throw new MissionStatusException(MISSION_CANNOT_BE_SCHEDULED);
        }
    }

    private void validateInProgressStatus(Mission mission) throws MissionStatusException {
        if (mission.getRockets().isEmpty() || mission.getRockets().stream()
                .anyMatch(rocket -> rocket.getStatus() == RocketStatus.IN_REPAIR)) {
            throw new MissionStatusException(MISSION_CANNOT_BE_IN_PROGRESS);
        }
    }

    public List<Mission> getMissionSummary() {
        Map<String, Mission> missions = missionRepository.getAllMissions();
        List<Mission> missionList = new ArrayList<>(missions.values());

        missionList.sort((m1, m2) -> {
            int rocketCountComparison = Integer.compare(m2.getRockets().size(), m1.getRockets().size());
            if (rocketCountComparison == 0) {
                return m2.getName().compareTo(m1.getName());
            }
            return rocketCountComparison;
        });

        return missionList;
    }

    private void updateMissionStatus(Mission mission) {
        if (mission.getRockets().isEmpty()) {
            // If no rockets are assigned, the mission should be "Scheduled"
            mission.setStatus(MissionStatus.SCHEDULED);
        } else {
            // Check if any rocket is in repair
            boolean anyRocketInRepair = mission.getRockets().stream()
                    .anyMatch(rocket -> rocket.getStatus() == RocketStatus.IN_REPAIR);

            if (anyRocketInRepair) {
                // If any rocket is in repair, the mission should be "Pending"
                mission.setStatus(MissionStatus.PENDING);
            } else {
                // If no rockets are in repair, the mission should be "In Progress"
                mission.setStatus(MissionStatus.IN_PROGRESS);
            }
        }
    }
}
