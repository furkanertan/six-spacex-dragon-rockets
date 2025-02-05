package service;

import domain.Mission;
import domain.MissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.MissionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MissionServiceTest {
    private MissionService missionService;
    private MissionRepository missionRepository;

    @BeforeEach
    void setUp() {
        missionRepository = Mockito.mock(MissionRepository.class);
        missionService = new MissionService(missionRepository);
    }

    @Test
    void createMission() {
        // Given & When
        Mission mission = missionService.createMission("Mars Mission");

        // Then
        assertNotNull(mission);
        assertEquals("Mars Mission", mission.getName());
        assertEquals(MissionStatus.SCHEDULED, mission.getStatus());
        verify(missionRepository, times(1)).addMission(any(Mission.class));
    }
}