package spacex.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spacex.domain.Mission;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MissionRepositoryTest {

    private MissionRepository missionRepository;
    private Mission mission1;
    private Mission mission2;

    @BeforeEach
    void setUp() {
        missionRepository = new MissionRepository();
        mission1 = new Mission("Apollo 11");
        mission2 = new Mission("Mars Rover");
    }

    @Test
    void should_Add_And_RetrieveMission() {
        // Given & When
        missionRepository.addMission(mission1);
        Mission createdMission = missionRepository.getMission("Apollo 11");

        // Then
        assertNotNull(createdMission);
        assertEquals("Apollo 11", createdMission.getName());
    }

    @Test
    void should_ReturnNull_IfMissionDoesNotExist() {
        // Given & When & Then
        assertNull(missionRepository.getMission("Voyager"));
    }

    @Test
    void should_GetAllMissions() {
        // Given
        missionRepository.addMission(mission1);
        missionRepository.addMission(mission2);

        // When
        Map<String, Mission> missions = missionRepository.getAllMissions();

        // Then
        assertEquals(2, missions.size());
        assertTrue(missions.containsKey("Apollo 11"));
        assertTrue(missions.containsKey("Mars Rover"));
    }
}