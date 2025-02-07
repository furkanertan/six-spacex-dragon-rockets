package spacex.service;

import lombok.AllArgsConstructor;
import spacex.domain.Mission;
import spacex.domain.MissionStatus;
import spacex.domain.Rocket;
import spacex.domain.RocketStatus;
import spacex.exception.MissionStatusException;
import spacex.exception.RocketAssignmentException;
import spacex.repository.MissionRepository;
import spacex.repository.RocketRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MissionRocketAssignmentService {

    private RocketRepository rocketRepository;
    private MissionRepository missionRepository;

    public void addRocket(Rocket rocket) {
        rocketRepository.addRocket(rocket);
    }

    public void assignRocketToMission(String rocketName, String missionName) throws RocketAssignmentException {
        Rocket rocket = rocketRepository.getRocket(rocketName);
        Mission mission = missionRepository.getMission(missionName);

        if (rocket == null || mission == null) {
            throw new RocketAssignmentException("Rocket or Mission not found");
        }

        if (rocket.getStatus() != RocketStatus.ON_GROUND) {
            throw new RocketAssignmentException("Rocket is not available for assignment");
        }

        rocket.setStatus(RocketStatus.IN_SPACE);
        mission.addRocket(rocket);
        updateMissionStatus(mission);
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

    public void addMission(Mission mission) {
        missionRepository.addMission(mission);
    }

    public void assignRocketsToMission(List<String> rocketNames, String missionName) throws RocketAssignmentException {
        Mission mission = missionRepository.getMission(missionName);
        if (mission == null) {
            throw new RocketAssignmentException("Mission not found");
        }

        for (String rocketName : rocketNames) {
            assignRocketToMission(rocketName, missionName);
        }
    }

    public void changeMissionStatus(String missionName, MissionStatus status) throws MissionStatusException {
        Mission mission = missionRepository.getMission(missionName);
        if (mission == null) {
            throw new MissionStatusException("Mission not found");
        }

        if (status == MissionStatus.ENDED && !mission.getRockets().isEmpty()) {
            throw new MissionStatusException("Cannot end mission with rockets assigned");
        }

        mission.setStatus(status);
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
