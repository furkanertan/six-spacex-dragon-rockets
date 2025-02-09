package spacex.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spacex.domain.Mission;
import spacex.domain.Rocket;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MissionSummaryFormatterTest {

    private Mission missionWithRockets;
    private Mission emptyMission;

    @BeforeEach
    void setUp() {
        Rocket rocket1 = new Rocket("Dragon XL");
        Rocket rocket2 = new Rocket("Falcon Heavy");

        missionWithRockets = new Mission("Transit");
        missionWithRockets.getRockets().addAll(List.of(rocket1, rocket2));

        emptyMission = new Mission("Mars");
    }

    @Test
    void should_FormatMission_WithRocketsCorrectly() {
        // Given
        String expected = """
                • Transit – Scheduled – Dragons: 2
                   • Dragon XL – On ground
                   • Falcon Heavy – On ground""";

        // When
        String result = MissionSummaryFormatter.formatMissions(List.of(missionWithRockets));

        // Then
        assertEquals(expected, result);
    }

    @Test
    void should_FormatEmptyMissionCorrectly() {
        //Given
        String expected = "• Mars – Scheduled – Dragons: 0";

        // When
        String result = MissionSummaryFormatter.formatMissions(List.of(emptyMission));

        // Then
        assertEquals(expected, result);
    }

    @Test
    void should_FormatMultipleMissionsCorrectly() {
        // Given
        Mission anotherMission = new Mission("Luna1");
        anotherMission.getRockets().add(new Rocket("Red Dragon"));

        String expected = """
                • Transit – Scheduled – Dragons: 2
                   • Dragon XL – On ground
                   • Falcon Heavy – On ground
                • Mars – Scheduled – Dragons: 0
                • Luna1 – Scheduled – Dragons: 1
                   • Red Dragon – On ground""";

        // When
        String result = MissionSummaryFormatter.formatMissions(List.of(missionWithRockets, emptyMission, anotherMission));

        // Then
        assertEquals(expected, result);
    }

    @Test
    void should_ReturnEmptyString_ForNoMissions() {
        // Given & When
        String result = MissionSummaryFormatter.formatMissions(List.of());

        // Then
        assertEquals("", result);
    }
}