package spacex;

import spacex.domain.Mission;
import spacex.domain.MissionStatus;
import spacex.domain.Rocket;
import spacex.domain.RocketStatus;
import spacex.exception.MissionStatusException;
import spacex.exception.RocketAssignmentException;
import spacex.repository.MissionRepository;
import spacex.repository.RocketRepository;
import spacex.service.MissionRocketAssignmentService;

import java.util.List;

public class SpaceXLibrary {

    private final MissionRocketAssignmentService missionRocketAssignmentService;

    public SpaceXLibrary() {
        RocketRepository rocketRepository = new RocketRepository();
        MissionRepository missionRepository = new MissionRepository();
        this.missionRocketAssignmentService = new MissionRocketAssignmentService(rocketRepository, missionRepository);
    }

    public void addRocket(Rocket rocket) {
        missionRocketAssignmentService.addRocket(rocket);
    }

    public void assignRocketToMission(String rocketName, String missionName) throws RocketAssignmentException {
        missionRocketAssignmentService.assignRocketToMission(rocketName, missionName);
    }

    public void changeRocketStatus(String rocketName, RocketStatus status) {
        missionRocketAssignmentService.changeRocketStatus(rocketName, status);
    }

    public void addMission(Mission mission) {
        missionRocketAssignmentService.addMission(mission);
    }

    public void assignRocketsToMission(List<String> rocketNames, String missionName) throws RocketAssignmentException {
        missionRocketAssignmentService.assignRocketsToMission(rocketNames, missionName);
    }

    public void changeMissionStatus(String missionName, MissionStatus status) throws MissionStatusException {
        missionRocketAssignmentService.changeMissionStatus(missionName, status);
    }

    public List<Mission> getMissionSummary() {
        return missionRocketAssignmentService.getMissionSummary();
    }
}
