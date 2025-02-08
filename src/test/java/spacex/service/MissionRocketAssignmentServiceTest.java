package spacex.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spacex.domain.Mission;
import spacex.domain.MissionStatus;
import spacex.domain.Rocket;
import spacex.domain.RocketStatus;
import spacex.exception.InvalidEntryException;
import spacex.exception.MissionStatusException;
import spacex.exception.RocketStatusException;
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
    void shouldAddRocket() throws InvalidEntryException {
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
    void shouldThrowExceptionWhenRocketWithSameNameAdded() throws InvalidEntryException {
        // Given
        Rocket rocket1 = new Rocket("Dragon 1");
        Rocket rocket2 = new Rocket("Dragon 1");

        missionRocketAssignmentService.addRocket(rocket1);

        // When & Then
        assertThrows(InvalidEntryException.class, () -> missionRocketAssignmentService.addRocket(rocket2));
    }

    @Test
    void shouldAddMission() throws InvalidEntryException {
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
    void shouldThrowExceptionWhenMissionWithSameNameAdded() throws InvalidEntryException {
        // Given
        Mission mission1 = new Mission("Mars");
        Mission mission2 = new Mission("Mars");

        missionRocketAssignmentService.addMission(mission1);

        // When & Then
        assertThrows(InvalidEntryException.class, () -> missionRocketAssignmentService.addMission(mission2));
    }

    @Test
    void shouldAssignRocketToMission() throws RocketStatusException, InvalidEntryException, MissionStatusException {
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
    void shouldThrowExceptionWhenRocketIsNotFound() throws InvalidEntryException {
        // Given
        Mission mission = new Mission("Mars");
        missionRocketAssignmentService.addMission(mission);

        // When & Then
        assertThrows(RocketStatusException.class, () -> missionRocketAssignmentService.assignRocketToMission("NonExistentRocket", mission.getName()));
    }

    @Test
    void shouldThrowExceptionWhenMissionIsNotFound() throws InvalidEntryException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(MissionStatusException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), "NonExistentMission"));
    }

    @Test
    void shouldThrowExceptionWhenRocketAssignedToEndedMission() throws InvalidEntryException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        Mission mission = new Mission("Mars");
        mission.setStatus(MissionStatus.ENDED);

        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(MissionStatusException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName()));
    }

    @Test
    void shouldThrowExceptionWhenInRepairRocketAssignedToMission() throws InvalidEntryException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        rocket.setStatus(RocketStatus.IN_REPAIR);

        Mission mission = new Mission("Jupiter");

        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(RocketStatusException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName()));
    }

    @Test
    void shouldThrowExceptionWhenAlreadyInSpaceRocketAssignedToMission() throws InvalidEntryException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        rocket.setStatus(RocketStatus.IN_SPACE);

        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket);

        // When & Then
        assertThrows(RocketStatusException.class, () -> missionRocketAssignmentService.assignRocketToMission(rocket.getName(), mission.getName()));
    }

    @Test
    void shouldAssignMultipleRocketsToMission() throws RocketStatusException, InvalidEntryException, MissionStatusException {
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
    void shouldChangeRocketStatus() throws InvalidEntryException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        missionRocketAssignmentService.addRocket(rocket);

        // When
        missionRocketAssignmentService.changeRocketStatus("Dragon 1", RocketStatus.IN_REPAIR);

        // Then
        assertEquals(RocketStatus.IN_REPAIR, rocket.getStatus());
    }

    @Test
    void shouldChangeMissionStatus() throws MissionStatusException, InvalidEntryException {
        // Given
        Mission mission = new Mission("Mars");
        missionRocketAssignmentService.addMission(mission);

        // When
        missionRocketAssignmentService.changeMissionStatus("Mars", MissionStatus.IN_PROGRESS);

        // Then
        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());
    }

    @Test
    void shouldEndMissionWithRocketsAssigned() throws RocketStatusException, InvalidEntryException, MissionStatusException {
        // Given
        Rocket rocket = new Rocket("Dragon 1");
        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket);
        missionRocketAssignmentService.addMission(mission);

        // When
        missionRocketAssignmentService.assignRocketToMission("Dragon 1", "Mars");

        // Then
        assertThrows(MissionStatusException.class, () -> missionRocketAssignmentService.changeMissionStatus("Mars", MissionStatus.ENDED));
    }

    @Test
    void shouldGetMissionSummary() throws RocketStatusException, InvalidEntryException, MissionStatusException {
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
    void shouldMissionStatusUpdatedWhenRocketStatusInRepair() throws RocketStatusException, InvalidEntryException, MissionStatusException {
        // Given
        Rocket rocket1 = new Rocket("Dragon 1");
        Rocket rocket2 = new Rocket("Dragon 2");
        Mission mission = new Mission("Mars");

        missionRocketAssignmentService.addRocket(rocket1);
        missionRocketAssignmentService.addRocket(rocket2);
        missionRocketAssignmentService.addMission(mission);

        missionRocketAssignmentService.assignRocketToMission("Dragon 1", "Mars");
        missionRocketAssignmentService.assignRocketToMission("Dragon 2", "Mars");

        assertEquals(MissionStatus.IN_PROGRESS, mission.getStatus());

        // When
        missionRocketAssignmentService.changeRocketStatus("Dragon 1", RocketStatus.IN_REPAIR);

        // Then
        assertEquals(MissionStatus.PENDING, mission.getStatus());
    }
}