package spacex.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spacex.domain.Mission;
import spacex.domain.MissionStatus;
import spacex.domain.Rocket;
import spacex.domain.RocketStatus;
import spacex.exception.SpaceXException;
import spacex.repository.MissionRepository;
import spacex.repository.RocketRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MissionRocketAssignmentServiceTest {

    private MissionRocketAssignmentService missionRocketAssignmentService;
    private RocketRepository rocketRepository;
    private MissionRepository missionRepository;

    @BeforeEach
    void setUp() {
        rocketRepository = new RocketRepository();
        missionRepository = new MissionRepository();
        missionRocketAssignmentService = new MissionRocketAssignmentService(rocketRepository, missionRepository);
    }

    @Test
    void should_AddRocket() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");

        // When
        missionRocketAssignmentService.addRocket(rocket);

        // Then
        Rocket createdRocket = rocketRepository.getRocket(rocket.getName());

        assertNotNull(createdRocket);
        assertEquals(rocket.getName(), createdRocket.getName());
        assertEquals(RocketStatus.ON_GROUND, createdRocket.getStatus());
    }

    @Test
    void should_ThrowException_WhenRocketWithSameNameAdded() throws SpaceXException {
        // Given
        Rocket rocket1 = new Rocket("Dragon 1");
        Rocket rocket2 = new Rocket("Dragon 1");

        missionRocketAssignmentService.addRocket(rocket1);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.addRocket(rocket2));
    }

    @Test
    void should_AddMission() throws SpaceXException {
        // Given
        Mission mission = new Mission("Mars");

        // When
        missionRocketAssignmentService.addMission(mission);

        // Then
        Mission createdMission = missionRepository.getMission(mission.getName());

        assertNotNull(createdMission);
        assertEquals(mission.getName(), createdMission.getName());
        assertEquals(MissionStatus.SCHEDULED, createdMission.getStatus());
    }

    @Test
    void should_ThrowException_WhenMissionWithSameNameAdded() throws SpaceXException {
        // Given
        Mission mission1 = new Mission("Mars");
        Mission mission2 = new Mission("Mars");

        missionRocketAssignmentService.addMission(mission1);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.addMission(mission2));
    }

    @Test
    void should_AssignRocketToMission() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket);
        missionRocketAssignmentService.addMission(mission);

        // When
        missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName());

        // Then
        assertEquals(RocketStatus.IN_SPACE, rocket.getStatus());
        assertEquals(1, mission.getRockets().size());

        assertTrue(mission.getRockets().contains(rocket));
        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());
    }

    @Test
    void should_ThrowException_WhenRocketIsNotFound() throws SpaceXException {
        // Given
        Mission mission = new Mission("Mars");
        missionRocketAssignmentService.addMission(mission);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.assignRocketToMission("NonExistentRocket", mission.getName()));
    }

    @Test
    void should_ThrowException_WhenMissionIsNotFound() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), "NonExistentMission"));
    }

    @Test
    void should_ThrowException_WhenRocket_AssignedToEndedMission() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        Mission mission = new Mission("Mars");
        mission.setStatus(MissionStatus.ENDED);

        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName()));
    }

    @Test
    void should_ThrowException_WhenInRepairRocket_AssignedToMission() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        rocket.setStatus(RocketStatus.IN_REPAIR);

        Mission mission = new Mission("Jupiter");

        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName()));
    }

    @Test
    void should_ThrowException_WhenAlreadyInSpaceRocket_AssignedToMission() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        rocket.setStatus(RocketStatus.IN_SPACE);

        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName()));
    }

    @Test
    void should_AssignMultipleRocketsToMission() throws SpaceXException {
        // Given
        Rocket rocket1 = new Rocket("Dragon 1");
        Rocket rocket2 = new Rocket("Dragon 2");
        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket1);
        missionRocketAssignmentService.addRocket(rocket2);
        missionRocketAssignmentService.addMission(mission);

        // When
        missionRocketAssignmentService.assignRocketsToMission(Arrays.asList("Dragon 1", "Dragon 2"), "Mars");

        // Then
        assertEquals(2, mission.getRockets().size());
        assertEquals(RocketStatus.IN_SPACE, rocket1.getStatus());
        assertEquals(RocketStatus.IN_SPACE, rocket2.getStatus());

        assertTrue(mission.getRockets().contains(rocket1));
        assertTrue(mission.getRockets().contains(rocket2));
        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());
    }

    @Test
    void should_ChangeRocketStatus() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        missionRocketAssignmentService.addRocket(rocket);

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_REPAIR);

        // Then
        assertEquals(RocketStatus.IN_REPAIR, rocket.getStatus());
    }

    @Test
    void should_ThrowException_WhenRocketStatusChangeInSpace_WithNoMissionAssigned() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_SPACE));
    }

    @Test
    void should_ChangeRocketStatusToInRepair_And_UpdateMissionStatusToPending() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket);

        Mission mission = new Mission("Mars 5");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_REPAIR);

        // When & Then
        assertEquals(1, mission.getRockets().size());
        assertEquals(RocketStatus.IN_REPAIR, rocket.getStatus());

        assertEquals(1, missionRepository.getAllMissions().size());
        Mission updatedMission = missionRepository.getMission(mission.getName());
        assertEquals(MissionStatus.PENDING, updatedMission.getStatus());
    }

    @Test
    void should_ChangeRocketStatusToOnGround_And_UpdateMissionStatusToPending() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket);

        Mission mission = new Mission("Mars 5");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_REPAIR);

        // When & Then
        assertEquals(1, mission.getRockets().size());
        assertEquals(RocketStatus.IN_REPAIR, rocket.getStatus());

        assertEquals(1, missionRepository.getAllMissions().size());
        Mission updatedMission = missionRepository.getMission(mission.getName());
        assertEquals(MissionStatus.PENDING, updatedMission.getStatus());
    }

    @Test
    void should_ChangeRocketStatusToInSpace_And_UpdateMissionStatusToInProgress() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket);

        Mission mission = new Mission("Mars 5");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName());

        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_REPAIR);

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_SPACE);

        // Then
        assertEquals(1, mission.getRockets().size());
        assertEquals(RocketStatus.IN_SPACE, rocket.getStatus());

        assertEquals(1, missionRepository.getAllMissions().size());
        Mission updatedMission = missionRepository.getMission(mission.getName());
        assertEquals(MissionStatus.IN_PROGRESS, updatedMission.getStatus());
    }

    @Test
    void should_ThrowException_WhenRocketStatusChange_FromInRepairToInSpace_WithNoMissionAssigned() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket);

        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_REPAIR);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_SPACE));
    }


    @Test
    void shouldChangeMissionStatus() throws SpaceXException {
        // Given
        Mission mission = new Mission("Mars");
        missionRocketAssignmentService.addMission(mission);

        // When
        missionRocketAssignmentService.changeMissionStatus("Mars", MissionStatus.IN_PROGRESS);

        // Then
        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());
    }

    @Test
    void shouldEndMissionWithRocketsAssigned() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket);
        missionRocketAssignmentService.addMission(mission);

        // When
        missionRocketAssignmentService.assignRocketToMission("Dragon 1", "Mars");

        // Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.changeMissionStatus("Mars", MissionStatus.ENDED));
    }

    @Test
    void shouldGetMissionSummary() throws SpaceXException {
        // Given
        Mission mission1 = new Mission("Mars");
        Mission mission2 = new Mission("Luna");
        Rocket rocket1 = new Rocket("Dragon 1");
        Rocket rocket2 = new Rocket("Dragon 2");

        missionRocketAssignmentService.addMission(mission1);
        missionRocketAssignmentService.addMission(mission2);
        missionRocketAssignmentService.addRocket(rocket1);
        missionRocketAssignmentService.addRocket(rocket2);

        missionRocketAssignmentService.assignRocketToMission("Dragon 1", "Mars");
        missionRocketAssignmentService.assignRocketToMission("Dragon 2", "Luna");

        // When
        List<Mission> summary = missionRocketAssignmentService.getMissionSummary();

        // Then
        assertEquals(2, summary.size());
        assertEquals("Mars", summary.get(0).getName()); // Mars has 1 rocket
        assertEquals("Luna", summary.get(1).getName()); // Luna has 1 rocket
    }

    @Test
    void shouldMissionStatusUpdatedWhenRocketStatusInRepair() throws SpaceXException {
        // Given
        Rocket rocket1 = new Rocket("Dragon 1");
        Rocket rocket2 = new Rocket("Dragon 2");
        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket1);
        missionRocketAssignmentService.addRocket(rocket2);
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketToMission(rocket1.getName(), mission.getName());
        missionRocketAssignmentService.assignRocketToMission(rocket2.getName(), mission.getName());

        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket1.getName(), RocketStatus.IN_REPAIR);

        // Then
        assertEquals(MissionStatus.PENDING, mission.getStatus());
    }
}