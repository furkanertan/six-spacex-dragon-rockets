package spacex.util;

import spacex.domain.Mission;
import spacex.domain.Rocket;

import java.util.List;
import java.util.stream.Collectors;

public class MissionSummaryFormatter {

    public static String formatMissions(List<Mission> missions) {
        return missions.stream()
                .map(MissionSummaryFormatter::formatMission)
                .collect(Collectors.joining("\n"));
    }

    private static String formatMission(Mission mission) {
        String missionHeader = String.format("• %s – %s – Dragons: %d",
                mission.getName(), mission.getStatus().getStatusName(), mission.getRockets().size());

        if (mission.getRockets().isEmpty()) {
            return missionHeader;
        }

        String rocketsInfo = mission.getRockets().stream()
                .map(MissionSummaryFormatter::formatRocket)
                .collect(Collectors.joining("\n", "\n", ""));

        return missionHeader + rocketsInfo;
    }

    private static String formatRocket(Rocket rocket) {
        return String.format("   • %s – %s", rocket.getName(), rocket.getStatus().getStatusName());
    }
}
