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
        Mission assignedMission = missionRepository.getMission(mission.getName());

        assertNotNull(assignedMission);
        assertEquals(mission.getName(), assignedMission.getName());
        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());

        List<Rocket> rockets = assignedMission.getRockets();

        assertNotNull(rockets);

        Rocket assignedRocket = rockets.get(0);
        assertEquals(RocketStatus.IN_SPACE, assignedRocket.getStatus());
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
    void should_ThrowException_WhenInRepairRocket_WithoutMission_AssignedToOtherMission() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        rocket.setStatus(RocketStatus.IN_REPAIR);

        missionRocketAssignmentService.addRocket(rocket);

        Mission mission = new Mission("Jupiter");
        missionRocketAssignmentService.addMission(mission);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName()));
    }

    @Test
    void should_ThrowException_WhenInSpaceRocket_WithoutMission_AssignedToMission() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        rocket.setStatus(RocketStatus.IN_SPACE);
        missionRocketAssignmentService.addRocket(rocket);

        Mission mission = new Mission("Mars");
        missionRocketAssignmentService.addMission(mission);

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
        missionRocketAssignmentService.assignRocketsToMission(Arrays.asList(rocket1.getName(), rocket2.getName()), mission.getName());

        // Then
        assertEquals(2, mission.getRockets().size());
        assertEquals(RocketStatus.IN_SPACE, rocket1.getStatus());
        assertEquals(RocketStatus.IN_SPACE, rocket2.getStatus());

        assertTrue(mission.getRockets().contains(rocket1));
        assertTrue(mission.getRockets().contains(rocket2));
        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());
    }

    @Test
    void should_ChangeRocketStatusToInRepair_And_UpdateMissionStatusToPending() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        missionRocketAssignmentService.addRocket(rocket);

        Mission mission = new Mission("Mars");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName());

        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());
        assertEquals(RocketStatus.IN_SPACE, rocket.getStatus());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_REPAIR);

        // Then
        Mission assignedMission = missionRepository.getMission(mission.getName());
        assertNotNull(assignedMission);

        List<Rocket> rockets = mission.getRockets();
        assertEquals(1, rockets.size());
        assertEquals(MissionStatus.PENDING, assignedMission.getStatus());

        Rocket assignedRocket = rockets.get(0);

        assertEquals(RocketStatus.IN_REPAIR, assignedRocket.getStatus());
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
    void should_ThrowException_WhenRocketStatusChangeInRepair_WithNoMissionAssigned() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(SpaceXException.class, () -> missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.IN_REPAIR));
    }

    @Test
    void should_ChangeRocketStatusToInSpace_And_UpdateMissionStatusToInProgress() throws SpaceXException {
        // Given
        Rocket rocket1 = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket1);

        Rocket rocket2 = new Rocket("Dragon Red");
        missionRocketAssignmentService.addRocket(rocket2);

        Mission mission = new Mission("Mars 5");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketsToMission(List.of(rocket1.getName(), rocket2.getName()), mission.getName());

        missionRocketAssignmentService.changeRocketStatus(rocket1.getName(), RocketStatus.IN_REPAIR);

        assertEquals(MissionStatus.PENDING, mission.getStatus());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket1.getName(), RocketStatus.IN_SPACE);

        // Then
        Mission assignedMission = missionRepository.getMission(mission.getName());
        assertNotNull(assignedMission);
        assertEquals(MissionStatus.IN_PROGRESS, assignedMission.getStatus());

        List<Rocket> rockets = mission.getRockets();
        assertEquals(2, rockets.size());

        assertEquals(RocketStatus.IN_SPACE, rockets.get(0).getStatus());
        assertEquals(RocketStatus.IN_SPACE, rockets.get(1).getStatus());
    }

    @Test
    void should_ChangeRocketStatusToInSpace_And_MissionStatusRemainPending() throws SpaceXException {
        // Given
        Rocket rocket1 = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket1);

        Rocket rocket2 = new Rocket("Dragon Red");
        missionRocketAssignmentService.addRocket(rocket2);

        Rocket rocket3 = new Rocket("Dragon Blue");
        missionRocketAssignmentService.addRocket(rocket3);

        Mission mission = new Mission("Venus");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketsToMission(List.of(rocket1.getName(), rocket2.getName(), rocket3.getName()), mission.getName());

        missionRocketAssignmentService.changeRocketStatus(rocket1.getName(), RocketStatus.IN_REPAIR);
        missionRocketAssignmentService.changeRocketStatus(rocket2.getName(), RocketStatus.IN_REPAIR);

        assertEquals(MissionStatus.PENDING, mission.getStatus());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket2.getName(), RocketStatus.IN_SPACE);

        // Then
        Mission assignedMission = missionRepository.getMission(mission.getName());
        assertNotNull(assignedMission);
        assertEquals(MissionStatus.PENDING, assignedMission.getStatus());

        List<Rocket> rockets = mission.getRockets();
        assertEquals(3, rockets.size());

        assertEquals(RocketStatus.IN_REPAIR, rockets.get(0).getStatus());
        assertEquals(RocketStatus.IN_SPACE, rockets.get(1).getStatus());
        assertEquals(RocketStatus.IN_SPACE, rockets.get(2).getStatus());
    }

    @Test
    void should_ChangeRocketStatusToOnGround_And_UpdateMissionStatusToEnded() throws SpaceXException {
        // Given
        Rocket rocket = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket);

        Mission mission = new Mission("Mars 5");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket.getName(), RocketStatus.ON_GROUND);

        // Then
        Mission assignedMission = missionRepository.getMission(mission.getName());
        assertNotNull(assignedMission);
        assertEquals(MissionStatus.ENDED, assignedMission.getStatus());

        List<Rocket> rockets = assignedMission.getRockets();
        assertEquals(0, rockets.size());
    }

    @Test
    void should_ChangeRocketStatusToOnGround_And_UpdateMissionStatusToInProgress() throws SpaceXException {
        // Given
        Rocket rocket1 = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket1);

        Rocket rocket2 = new Rocket("Faze");
        missionRocketAssignmentService.addRocket(rocket2);

        Mission mission = new Mission("Mars 5");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketsToMission(List.of(rocket1.getName(), rocket2.getName()), mission.getName());

        missionRocketAssignmentService.changeRocketStatus(rocket2.getName(), RocketStatus.IN_REPAIR);

        assertEquals(MissionStatus.PENDING, mission.getStatus());
        assertEquals(2, mission.getRockets().size());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket2.getName(), RocketStatus.ON_GROUND);

        // Then
        Mission assignedMission = missionRepository.getMission(mission.getName());
        assertNotNull(assignedMission);
        assertEquals(MissionStatus.IN_PROGRESS, assignedMission.getStatus());

        List<Rocket> rockets = assignedMission.getRockets();
        assertEquals(1, rockets.size());

        Rocket rocket = rockets.get(0);
        assertEquals(RocketStatus.IN_SPACE, rocket.getStatus());
    }

    @Test
    void should_ChangeRocketStatusToOnGround_And_MissionStatusRemainPending() throws SpaceXException {
        // Given
        Rocket rocket1 = new Rocket("Dragon XL");
        missionRocketAssignmentService.addRocket(rocket1);

        Rocket rocket2 = new Rocket("Faze");
        missionRocketAssignmentService.addRocket(rocket2);

        Mission mission = new Mission("Mars 5");
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketsToMission(List.of(rocket1.getName(), rocket2.getName()), mission.getName());

        missionRocketAssignmentService.changeRocketStatus(rocket2.getName(), RocketStatus.IN_REPAIR);

        assertEquals(MissionStatus.PENDING, mission.getStatus());
        assertEquals(2, mission.getRockets().size());

        // When
        missionRocketAssignmentService.changeRocketStatus(rocket1.getName(), RocketStatus.ON_GROUND);

        // Then
        Mission assignedMission = missionRepository.getMission(mission.getName());
        assertNotNull(assignedMission);
        assertEquals(MissionStatus.PENDING, assignedMission.getStatus());

        List<Rocket> rockets = assignedMission.getRockets();
        assertEquals(1, rockets.size());

        Rocket rocket = rockets.get(0);
        assertEquals(RocketStatus.IN_REPAIR, rocket.getStatus());
    }

    @Test
    void should_GetMissionSummary() throws SpaceXException {
        // Given
        Mission mission1 = new Mission("Mars");
        Mission mission2 = new Mission("Luna");
        Rocket rocket1 = new Rocket("Dragon 1");
        Rocket rocket2 = new Rocket("Dragon 2");

        String expected = """
                • Mars – In Progress – Dragons: 1
                   • Dragon 1 – In space
                • Luna – In Progress – Dragons: 1
                   • Dragon 2 – In space""";

        missionRocketAssignmentService.addMission(mission1);
        missionRocketAssignmentService.addMission(mission2);
        missionRocketAssignmentService.addRocket(rocket1);
        missionRocketAssignmentService.addRocket(rocket2);

        missionRocketAssignmentService.assignRocketToMission(rocket1.getName(), mission1.getName());
        missionRocketAssignmentService.assignRocketToMission(rocket2.getName(), mission2.getName());

        // When
        String summary = missionRocketAssignmentService.getMissionSummary();

        // Then
        assertEquals(expected, summary);
    }
}