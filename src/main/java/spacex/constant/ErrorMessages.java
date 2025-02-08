package spacex.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorMessages {

    public static final String ROCKET_NOT_FOUND = "Rocket is not found!";
    public static final String ROCKET_NOT_AVAILABLE = "Rocket is not available for assignment!";
    public static final String ROCKET_ALREADY_EXISTS = "Rocket already exists!";

    public static final String MISSION_NOT_FOUND = "Mission is not found!";
    public static final String MISSION_NOT_AVAILABLE = "Mission is not available for assignment!";
    public static final String MISSION_ALREADY_EXISTS = "Mission already ended!";
    public static final String MISSION_CANNOT_END = "Mission cannot be set to END with rockets assigned!";
    public static final String MISSION_CANNOT_BE_PENDING = "Mission cannot be set to Pending without a rocket in repair!";
    public static final String MISSION_CANNOT_BE_SCHEDULED = "Mission cannot be set to Scheduled with rockets assigned!";
    public static final String MISSION_CANNOT_BE_IN_PROGRESS = "Mission cannot be set to In Progress with rockets in repair or no rockets assigned!";
}