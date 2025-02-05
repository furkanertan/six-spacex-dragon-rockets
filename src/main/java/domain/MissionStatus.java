package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MissionStatus {
    SCHEDULED("Scheduled"),
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    ENDED("Ended");

    private final String status;
}
