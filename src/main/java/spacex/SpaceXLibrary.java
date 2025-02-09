package spacex;

import spacex.domain.Mission;
import spacex.domain.Rocket;
import spacex.domain.RocketStatus;
import spacex.exception.SpaceXException;
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

    public void addRocket(Rocket rocket) throws SpaceXException {
        missionRocketAssignmentService.addRocket(rocket);
    }

    public void addMission(Mission mission) throws SpaceXException {
        missionRocketAssignmentService.addMission(mission);
    }

    public void assignRocketToMission(String rocketName, String missionName) throws SpaceXException {
        missionRocketAssignmentService.assignRocketToMission(rocketName, missionName);
    }

    public void assignRocketsToMission(List<String> rocketNames, String missionName) throws SpaceXException {
        missionRocketAssignmentService.assignRocketsToMission(rocketNames, missionName);
    }

    public void changeRocketStatus(String rocketName, RocketStatus status) throws SpaceXException {
        missionRocketAssignmentService.changeRocketStatus(rocketName, status);
    }

    public String getMissionSummary() {
        return missionRocketAssignmentService.getMissionSummary();
    }
}
